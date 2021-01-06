package de.uol.swp.common.message;

/**
 * Base class of all request messages. Basic handling of messages from the client
 * to the server
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.message.AbstractMessage
 * @see de.uol.swp.common.message.RequestMessage
 * @since 2019-08-07
 */
public abstract class AbstractRequestMessage extends AbstractMessage implements RequestMessage {

    @Override
    public boolean authorisationNeeded() {
        return true;
    }
}
