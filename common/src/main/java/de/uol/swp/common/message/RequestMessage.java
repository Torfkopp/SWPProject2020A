package de.uol.swp.common.message;

/**
 * A base interface for all messages from client to server
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.message.Message
 * @since 2019-08-07
 */

public interface RequestMessage extends Message {

    /**
     * States if this request can only be used by
     * authorised users (typically has a valid auth)
     *
     * @return True if valid authorisation is needed
     * @since 2019-08-07
     */
    boolean authorisationNeeded();
}
