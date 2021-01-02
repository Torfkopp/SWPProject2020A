package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.User;

/**
 * Message sent by the server when a user successfully joins a lobby
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.message.AbstractLobbyMessage
 * @see de.uol.swp.common.user.User
 * @since 2019-10-08
 */
public class UserJoinedLobbyMessage extends AbstractLobbyMessage {
    /**
     * Default constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2019-10-08
     */
    public UserJoinedLobbyMessage() {
    }

    /**
     * Constructor
     *
     * @param lobbyName name of the lobby
     * @param user      user who joined the lobby
     * @since 2019-10-08
     */
    public UserJoinedLobbyMessage(String lobbyName, User user) {
        super(lobbyName, user);
    }
}
