package de.uol.swp.server.communication.netty;

import de.uol.swp.common.MyObjectDecoder;
import de.uol.swp.common.MyObjectEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;

/**
 * This class handles opening a port clients can connect to.
 *
 * @author Marco Grawunder
 * @since 2019-11-20
 */
public class Server {

    private static final Logger LOG = LogManager.getLogger(Server.class);
    private final ChannelHandler serverHandler;

    /**
     * Constructor
     * <p>
     * Creates a new Server object
     *
     * @see io.netty.channel.ChannelHandler
     * @see de.uol.swp.server.communication.ServerHandler
     * @since 2019-11-20
     */
    public Server(ChannelHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    /**
     * Start a new server on the given port
     *
     * @param port Port number the server shall be reachable on
     *
     * @throws java.lang.InterruptedException Server failed to start, e.g. the port is already in use
     * @see java.net.InetSocketAddress
     * @since 2019-11-20
     */
    public void start(int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
             .localAddress(new InetSocketAddress(port)).childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws CertificateException, SSLException {
                    // Create and add SslHandler accordingly
                    SelfSignedCertificate cert = new SelfSignedCertificate(); // Why is this not working? :(
                    SslContext context = SslContextBuilder.forServer(cert.privateKey(), cert.certificate()).build();
                    SSLEngine engine = context.newEngine(ch.alloc());
                    ch.pipeline().addLast(new SslHandler(engine));
                    // Add IdleStateHandler to handle timeouts
                    ch.pipeline().addLast(new IdleStateHandler(70, 20, 0));
                    // Encoder and decoder are both needed!
                    // Send and receive serialisable objects
                    ch.pipeline().addLast(new MyObjectEncoder());
                    ch.pipeline().addLast(new MyObjectDecoder(ClassResolvers.cacheDisabled(null)));
                    // Must be last in the pipeline, else they will not
                    // get encoded/decoded objects but ByteBuf
                    ch.pipeline().addLast(serverHandler);
                }
            });
            // Just wait for server shutdown
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }
}
