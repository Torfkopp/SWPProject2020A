package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.UserDTO;

/**
 * Request sent to the server when a user wants to leave a lobby
 *
 * @see AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
public class LobbyLeaveUserRequest extends AbstractLobbyRequest {

    /**
     * Default constructor
     *
     * @implNote This constructor is needed for serialisation
     * @since 2019-10-08
     */
    public LobbyLeaveUserRequest() {
    }

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     * @param user User who wants to leave the lobby
     * @since 2019-10-08
     */
    public LobbyLeaveUserRequest(String lobbyName, UserDTO user) {
        super(lobbyName, user);
    }

}
