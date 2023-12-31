package de.uol.swp.server.game.event;

import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.user.Actor;
import de.uol.swp.server.message.AbstractServerInternalMessage;

/**
 * This Event is sent to the LobbyService to get
 * the Session of the targetUser and stores a
 * ResponseMessage which is to be sent to the targetUser
 * via the ServerHandler.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.server.message.AbstractServerInternalMessage
 * @since 2021-02-25
 */
public class ForwardToUserInternalRequest extends AbstractServerInternalMessage {

    private final Actor targetUser;
    private final ResponseMessage responseMessage;

    /**
     * Constructor
     *
     * @param targetUser      User object of the targetUser
     * @param responseMessage Stored ResponseMessage
     */
    public ForwardToUserInternalRequest(Actor targetUser, ResponseMessage responseMessage) {
        this.responseMessage = responseMessage;
        this.targetUser = targetUser;
    }

    /**
     * Gets the stored ResponseMessage
     *
     * @return The stored ResponseMessage
     */
    public ResponseMessage getResponseMessage() {
        return responseMessage;
    }

    /**
     * Gets the User to find his Session
     *
     * @return A User-Object of the target User
     */
    public Actor getTargetUser() {
        return targetUser;
    }
}
