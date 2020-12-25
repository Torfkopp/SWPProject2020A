package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.request.AbstractLobbyRequest;
import de.uol.swp.common.user.UserDTO;

/**
 * Request sent to the server when a user wants to join a lobby
 *
 * @see AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
public class LobbyJoinUserRequest extends AbstractLobbyRequest {

    /**
     * Default constructor
     *
     * @implNote This constructor is needed for serialisation
     * @since 2019-10-08
     */
    public LobbyJoinUserRequest() {
    }
    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     * @param user User who wants to join the lobby
     * @since 2019-10-08
     */
    public LobbyJoinUserRequest(String lobbyName, UserDTO user) {
        super(lobbyName, user);
    }

}
