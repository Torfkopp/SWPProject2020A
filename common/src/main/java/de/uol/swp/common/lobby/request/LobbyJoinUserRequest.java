package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.UserOrDummy;

/**
 * Request sent to the server when a user wants to join a lobby
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @since 2019-10-08
 */
public class LobbyJoinUserRequest extends AbstractLobbyRequest {

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     * @param user      User who wants to join the lobby
     *
     * @since 2019-10-08
     */
    public LobbyJoinUserRequest(String lobbyName, UserOrDummy user) {
        super(lobbyName, user);
    }
}
