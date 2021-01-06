package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.User;

/**
 * Message sent by the server when a user leaves a lobby successfully.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.message.AbstractLobbyMessage
 * @see de.uol.swp.common.user.User
 * @since 2019-10-08
 */
public class UserLeftLobbyMessage extends AbstractLobbyMessage {

    /**
     * Default constructor
     *
     * @implNote This constructor is needed for serialisation
     * @since 2019-10-08
     */
    public UserLeftLobbyMessage() {
    }

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     * @param user      User who left the lobby
     * @since 2019-10-08
     */
    public UserLeftLobbyMessage(String lobbyName, User user) {
        super(lobbyName, user);
    }
}
