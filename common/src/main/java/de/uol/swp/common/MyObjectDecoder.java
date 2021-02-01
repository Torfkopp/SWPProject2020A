package de.uol.swp.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class used to decode an incoming ByteBuffer into an object.
 * <p>
 * An object of this class is used in the start methods of de.uol.swp.client.ClientConnection
 * and de.uol.swp.server.communication.Server
 *
 * @author Marco Grawunder
 * @see io.netty.handler.codec.serialization.ObjectDecoder
 * @since 2019-08-13
 */
public class MyObjectDecoder extends ObjectDecoder {

    private static final Logger LOG = LogManager.getLogger(MyObjectDecoder.class);

    /**
     * Constructor
     *
     * @param classResolver The ClassResolver the decoder should use
     *
     * @since 2019-08-13
     */
    public MyObjectDecoder(ClassResolver classResolver) {
        super(classResolver);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded;
        try {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Trying to decode: " + in);
            }
            decoded = super.decode(ctx, in);
            if (LOG.isTraceEnabled()) {
                LOG.trace(in + " " + decoded);
            }
        } catch (Exception e) {
            LOG.error(e);
            throw e;
        }
        return decoded;
    }
}
