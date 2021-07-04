package de.uol.swp.server.communication.netty;

import de.uol.swp.common.NettyObjectDecoder;
import de.uol.swp.common.NettyObjectEncoder;
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
     * @throws java.lang.InterruptedException          Server failed to start, e.g. the port is already in use
     * @throws java.security.cert.CertificateException Server failed to create a certificate
     * @see java.net.InetSocketAddress
     * @since 2019-11-20
     */
    public void start(int port) throws InterruptedException, CertificateException {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
             .localAddress(new InetSocketAddress(port)).childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws SSLException {
                    // Create and add SslHandler accordingly
                    LOG.trace("Adding SSLHandler to pipeline");
                    SslContext context = SslContextBuilder.forServer(cert.key(), cert.cert()).build();
                    SSLEngine engine = context.newEngine(ch.alloc());
                    ch.pipeline().addLast(new SslHandler(engine));
                    // Add IdleStateHandler to handle timeouts
                    LOG.trace("Adding IdleStateHandler to pipeline");
                    ch.pipeline().addLast(new IdleStateHandler(70, 20, 0));
                    // Encoder and decoder are both needed!
                    // Send and receive serialisable objects
                    LOG.trace("Adding Encoder and Decoder to pipeline");
                    ch.pipeline().addLast(new NettyObjectEncoder());
                    ch.pipeline().addLast(new NettyObjectDecoder(ClassResolvers.cacheDisabled(null)));
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
