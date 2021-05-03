package de.uol.swp.common.lobby.response;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.lobby.ISimpleLobby;

/**
 * Response sent by the server when a user wants to join a lobby
 *
 * @author Marvin Drees
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @see de.uol.swp.common.lobby.request.LobbyJoinUserRequest
 * @since 2020-12-21
 */
public class JoinLobbyResponse extends AbstractLobbyResponse {

    private final ISimpleLobby lobby;

    /**
     * Constructor
     *
     * @param lobbyName The name for the new lobby
     * @param lobby     The object of the joined lobby
     **/
    public JoinLobbyResponse(LobbyName lobbyName, ISimpleLobby lobby) {
        super(lobbyName);
        this.lobby = lobby;
    }

    /**
     * Gets the lobby where the user joined.
     *
     * @return The lobby the user joined
     *
     * @author Maximilian Lindner
     * @author AldinDervisi
     * @since 2021-03-14
     */
    public ISimpleLobby getLobby() {
        return lobby;
    }
}
