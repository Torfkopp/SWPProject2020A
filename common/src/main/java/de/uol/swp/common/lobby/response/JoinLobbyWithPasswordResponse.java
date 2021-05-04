package de.uol.swp.common.lobby.response;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.lobby.ISimpleLobby;

/**
 * Response sent by the server when a user wants to join a lobby with a password
 *
 * @author Alwin Bossert
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @see de.uol.swp.common.lobby.request.LobbyJoinUserRequest
 * @since 2021-04-22
 */
public class JoinLobbyWithPasswordResponse extends AbstractLobbyResponse {

    private final ISimpleLobby lobby;

    /**
     * Constructor
     *
     * @param lobbyName The name for the new lobby
     * @param lobby     The object of the joined lobby
     **/
    public JoinLobbyWithPasswordResponse(LobbyName lobbyName, ISimpleLobby lobby) {
        super(lobbyName);
        this.lobby = lobby;
    }

    /**
     * Gets the lobby where the user joined.
     *
     * @return The lobby the user joined
     *
     * @author Alwin Bossert
     */
    public ISimpleLobby getLobby() {
        return lobby;
    }
}
