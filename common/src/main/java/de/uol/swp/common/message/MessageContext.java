package de.uol.swp.common.message;

import java.io.Serializable;

/**
 * Interface to encapsulate different types of MessageContexts
 * <p>
 * In the base project the only implementation of this interface is the NettyMessageContext
 * within the communication package of the server
 *
 * @author Marco Grawunder
 * @see java.io.Serializable
 * @since 2019-08-13
 */
public interface MessageContext extends Serializable {

    /**
     * Sends a ResponseMessage
     *
     * @param message The message that should be sent
     *
     * @since 2019-11-20
     */
    void writeAndFlush(ResponseMessage message);

    /**
     * Sends a ServerMessage
     *
     * @param message The server message that should be sent
     *
     * @since 2019-11-20
     */
    void writeAndFlush(ServerMessage message);
}
