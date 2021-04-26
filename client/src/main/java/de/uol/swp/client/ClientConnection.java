package de.uol.swp.client;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uol.swp.client.main.events.ClientDisconnectedFromServerEvent;
import de.uol.swp.common.MyObjectDecoder;
import de.uol.swp.common.exception.ExceptionMessage;
import de.uol.swp.common.message.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The ClientConnection class
 * <p>
 * This Class manages connecting to a server, disconnecting from the server and
 * handling of incoming and outgoing messages.
 *
 * @author Marco Grawunder
 * @since 2017-03-17
 */
@SuppressWarnings("UnstableApiUsage")
public class ClientConnection {

    private static final Logger LOG = LogManager.getLogger(ClientConnection.class);

    private final String host;
    private final int port;
    private final List<ConnectionListener> connectionListener = new CopyOnWriteArrayList<>();
    private EventLoopGroup group;
    private EventBus eventBus;
    private Channel channel;

    /**
     * Creates a new connection to a specific port on the given host
     *
     * @param host The server name or IP to connect to
     * @param port The server port to connect to
     *
     * @since 2017-03-17
     */
    @Inject
    public ClientConnection(@Assisted String host, @Assisted int port, EventBus eventBus) {
        this.host = host;
        this.port = port;
        setEventBus(eventBus);
    }

    /**
     * Add a new ConnectionListener to the ConnectionListener array of this object
     *
     * @param listener The ConnectionListener to add to the array
     *
     * @see de.uol.swp.client.ConnectionListener
     * @since 2017-03-17
     */
    public void addConnectionListener(ConnectionListener listener) {
        this.connectionListener.add(listener);
    }

    /**
     * Disconnects the client from the server
     * <p>
     * Disconnects the client from the server and prints the stack trace
     * if an InterruptedException is thrown.
     *
     * @since 2017-03-17
     */
    public void close() {
        try {
            group.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the distribution of throwable messages
     * <p>
     * This method distributes throwable messages to the ConnectionListeners.
     * It calls the ExceptionOccurred method of every ConnectionListener in the
     * ConnectionListener array and passes the message to them.
     *
     * @param message The ExceptionMessage object found on the EventBus
     *
     * @see de.uol.swp.client.ClientHandler
     * @since 2017-03-17
     */
    public void process(Throwable message) {
        for (ConnectionListener l : connectionListener) {
            l.exceptionOccurred(message.getMessage());
        }
    }

    /**
     * Processes incoming messages
     * <p>
     * This method posts the message it gets onto the EventBus
     * if it is a ServerMessage or a ResponseMessage.
     * It writes "Received message. Post on event bus " plus
     * the Message to the LOG if the LOG-Level is set to DEBUG or higher.
     * If it is a different kind of message, it gets discarded.
     * With LOG-Level set to WARN or higher
     * "Can only process ServerMessage and ResponseMessage. Received: "
     * and the message are written to the LOG.
     *
     * @param in The incoming messages read by the ClientHandler
     *
     * @see de.uol.swp.client.ClientHandler
     * @since 2017-03-17
     */
    public void receivedMessage(Message in) {
        if (in instanceof ServerMessage || in instanceof ResponseMessage) {
            eventBus.post(in);
        } else {
            LOG.warn("Can only process ServerMessage and ResponseMessage.");
        }
    }

    /**
     * Handles the reset of the current Client when timing out.
     * <p>
     * This method shuts down the old client when the Client has no active
     * connection to the server anymore
     *
     * @author Marvin Drees
     * @author Aldin Dervisi
     * @see de.uol.swp.client.main.events.ClientDisconnectedFromServerEvent
     * @since 2021-03-18
     */
    public void resetClient() {
        eventBus.post(new ClientDisconnectedFromServerEvent());
    }

    /**
     * Sets the EventBus for the object
     * <p>
     * Sets the EventBus for the object and registers the object to it.
     *
     * @param eventBus The new EventBus to set
     *
     * @implNote If the object already has an EventBus, it is replaced but not unregistered
     * @since 2019-09-18
     */
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    /**
     * The netty init method
     * <p>
     * The example method on how to initialise a connection to a server via netty.
     * Multiple settings are made inside the ChannelInitializer with the {@code
     * pipeline.addLast()} method. Things usually added are encoders, decoders, and
     * the ChannelHandler.
     *
     * @throws java.lang.InterruptedException Connection failed
     * @implNote If no ChannelHandler is added, communication will not be possible
     * @since 2017-03-17
     */
    public void start() throws InterruptedException {
        group = new NioEventLoopGroup();
        try {
            Bootstrap bill = new Bootstrap();
            bill.group(group).channel(NioSocketChannel.class).remoteAddress(new InetSocketAddress(host, port))
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     // Add IdleStateHandler to handle timeouts
                     ch.pipeline().addLast(new IdleStateHandler(65, 0, 0));
                     // Add both Encoder and Decoder to send and receive serialisable objects
                     ch.pipeline().addLast(new ObjectEncoder());
                     ch.pipeline().addLast(new MyObjectDecoder(ClassResolvers.cacheDisabled(null)));
                     // Add a ClientHandler
                     ch.pipeline().addLast(new ClientHandler(ClientConnection.this));
                 }
             });
            ChannelFuture f = bill.connect().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    /**
     * Calls the ConnectionEstablished method of every ConnectionListener added
     * to this class.
     *
     * @param channel The netty channel the new connection is established on
     *
     * @see de.uol.swp.client.ConnectionListener
     * @since 2017-03-17
     */
    void fireConnectionEstablished(Channel channel) {
        for (ConnectionListener listener : connectionListener) {
            listener.connectionEstablished(channel);
        }
        this.channel = channel;
    }

    /**
     * Handles errors produced by the EventBus
     * <p>
     * If an DeadEvent object is detected on the EventBus, this method is called.
     * It writes "DeadEvent detected " and the error message of the detected DeadEvent
     * object to the log if the loglevel is set to WARN or higher.
     *
     * @param deadEvent The DeadEvent object found on the EventBus
     *
     * @since 2017-03-17
     */
    @Subscribe
    private void onDeadEvent(DeadEvent deadEvent) {
        LOG.warn("DeadEvent detected: {}", deadEvent);
    }

    /**
     * Handles a ExceptionMessage found on the EventBus
     * <p>
     * If an ExceptionMessage object is detected on the EventBus, this method is called.
     * It calls the exceptionOccurred method of every ConnectionListener in the
     * ConnectionListener array.
     *
     * @param message The ExceptionMessage object found on the EventBus
     *
     * @see de.uol.swp.common.exception.ExceptionMessage
     * @since 2017-03-17
     */
    @Subscribe
    private void onExceptionMessage(ExceptionMessage message) {
        for (ConnectionListener l : connectionListener) {
            l.exceptionOccurred(message.getException());
        }
    }

    /**
     * Handles a PingMessage found on the EventBus
     * <p>
     * If a PingMessage object is found on the EventBus, this method is called.
     * It responds by posting a PongMessage on the EventBus.
     *
     * @param msg The PingMessage object found on the EventBus
     *
     * @author Aldin Dervisi
     * @author Marvin Drees
     * @see de.uol.swp.common.message.PingMessage
     * @see de.uol.swp.common.message.PongMessage
     * @since 2021-03-18
     */
    @Subscribe
    private void onPingMessage(PingMessage msg) {
        LOG.trace("Server ping received.");
        eventBus.post(new PongMessage());
    }

    /**
     * Handles a RequestMessage detected on the EventBus
     * <p>
     * If the client is connected to the server and the channel of this object
     * is set, the RequestMessage given to this method is sent to the server.
     * Otherwise, "Several tries to send a message, but server is not connected" is
     * written to the LOG if the LOG-Level is set to WARN or higher.
     *
     * @param message The RequestMessage object found on the EventBus
     *
     * @see de.uol.swp.common.message.RequestMessage
     * @since 2019-08-29
     */
    @Subscribe
    private void onRequestMessage(RequestMessage message) {
        if (channel != null) {
            channel.writeAndFlush(message);
        } else {
            LOG.warn("Several tries to send a message, but server is not connected.");
            // TODO: may create stack trace?
        }
    }
}
