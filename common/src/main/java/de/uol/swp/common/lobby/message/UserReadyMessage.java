package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.User;

/**
 * Message sent by the server when a user changes their Ready status successfully.
 * <p>
 * This request is used both when a user changes to Ready as well as when a
 * user changes to not Ready.
 *
 * @author Eric Vuong
 * @author Maximilian Lindner
 * @see de.uol.swp.common.lobby.message.AbstractLobbyMessage
 * @see de.uol.swp.common.lobby.request.UserReadyRequest
 * @since 2021-01-19
 */
public class UserReadyMessage extends AbstractLobbyMessage {

    /**
     * Constructor
     *
     * @param lobbyName The Name of the lobby
     * @param user      The User who changed their ready status
     */
    public UserReadyMessage(String lobbyName, User user) {
        super(lobbyName, user);
    }
}
