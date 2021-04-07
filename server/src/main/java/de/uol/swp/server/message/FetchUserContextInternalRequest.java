package de.uol.swp.server.message;

import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.sessions.Session;

/**
 * This request is sent to the ServerHandler to get a
 * MessageContext of a specific userSession. It also stores
 * a ResponseMessage.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.server.communication.ServerHandler
 * @see de.uol.swp.server.message.AbstractServerInternalMessage
 * @since 2021 -02-25
 */
public class FetchUserContextInternalRequest extends AbstractServerInternalMessage {

    private final Session userSession;
    private final ResponseMessage returnMessage;

    /**
     * Constructor.
     *
     * @param userSession   The user session of the target user
     * @param returnMessage The stored ResponseMessage
     */
    public FetchUserContextInternalRequest(Session userSession, ResponseMessage returnMessage) {
        this.userSession = userSession;
        this.returnMessage = returnMessage;
    }

    /**
     * Gets stored return message.
     *
     * @return The stored return message
     */
    public ResponseMessage getReturnMessage() {
        return returnMessage;
    }

    /**
     * Gets the user session
     *
     * @return The userSession
     */
    public Session getUserSession() {
        return userSession;
    }
}
