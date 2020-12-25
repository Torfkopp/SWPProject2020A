package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.UserDTO;

/**
 * Message sent by the server when a user joins a lobby successfully.
 *
 * @see de.uol.swp.common.lobby.message.AbstractLobbyMessage
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
public class UserJoinedLobbyMessage extends AbstractLobbyMessage {
    /**
     * Default constructor
     *
     * @implNote This constructor is needed for serialisation
     * @since 2019-10-08
     */
    public UserJoinedLobbyMessage() { }

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     * @param user User who joined the lobby
     * @since 2019-10-08
     */
    public UserJoinedLobbyMessage(String lobbyName, UserDTO user) {
        super(lobbyName, user);
    }
}
