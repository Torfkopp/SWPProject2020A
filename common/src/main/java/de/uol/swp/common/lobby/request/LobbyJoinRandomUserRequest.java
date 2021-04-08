package de.uol.swp.common.lobby.request;


import de.uol.swp.common.user.UserOrDummy;

/**
 * Request sent to the server when a user wants to join a random lobby
 *
 * @author Finn Haase
 * @author Sven Ahrens
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-04-08
 */
public class LobbyJoinRandomUserRequest extends AbstractLobbyRequest {

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     * @param user      User who wants to join a random lobby
     *
     * @since 2021-04-08
     */
    public LobbyJoinRandomUserRequest(String lobbyName, UserOrDummy user) {
        super(lobbyName, user);
    }

}
