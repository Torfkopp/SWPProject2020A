package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.UserOrDummy;

/**
 * Message sent by the server when a user joins a lobby successfully.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.message.AbstractLobbyMessage
 * @see de.uol.swp.common.user.User
 * @since 2019-10-08
 */
public class UserJoinedLobbyMessage extends AbstractLobbyMessage {

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     * @param user      User who joined the lobby
     *
     * @since 2019-10-08
     */
    public UserJoinedLobbyMessage(String lobbyName, UserOrDummy user) {
        super(lobbyName, user);
    }
}
