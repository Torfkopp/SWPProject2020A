package de.uol.swp.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

/**
 * Class used to encode an object into a sendable ByteBuffer.
 * <p>
 * An object of this class is used in the start method of de.uol.swp.server.communication.Server
 *
 * @author Marco Grawunder
 * @see io.netty.handler.codec.serialization.ObjectEncoder
 * @since 2019-08-13
 */
public class MyObjectEncoder extends ObjectEncoder {

    private static final Logger LOG = LogManager.getLogger(MyObjectEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
        LOG.trace("Trying to encode: {}", msg);
        try {
            super.encode(ctx, msg, out);
        } catch (Exception e) {
            LOG.error(e);
            e.printStackTrace();
            throw e;
        }
        LOG.trace("{} {}", msg, out);
    }
}
