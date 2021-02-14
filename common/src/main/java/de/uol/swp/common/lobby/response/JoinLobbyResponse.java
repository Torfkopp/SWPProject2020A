package de.uol.swp.common.lobby.response;

/**
 * Response sent by the server when a user wants to join a lobby
 *
 * @author Marvin Drees
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @see de.uol.swp.common.lobby.request.LobbyJoinUserRequest
 * @since 2020-12-21
 */
public class JoinLobbyResponse extends AbstractLobbyResponse {

    /**
     * Constructor
     *
     * @param lobbyName The name for the new lobby
     **/
    public JoinLobbyResponse(String lobbyName) {
        super(lobbyName);
    }
}
