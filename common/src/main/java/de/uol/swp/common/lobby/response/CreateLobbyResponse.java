package de.uol.swp.common.lobby.response;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.lobby.Lobby;

/**
 * Response sent by the server to a user who created a lobby
 *
 * @author Alwin Bossert
 * @author Steven Luong
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @see de.uol.swp.common.lobby.request.CreateLobbyRequest
 * @since 2020-12-21
 */
public class CreateLobbyResponse extends AbstractLobbyResponse {

    private final Lobby lobby;

    /**
     * Constructor
     *
     * @param lobbyName The name for the new lobby
     * @param lobby     The object of the created lobby
     **/
    public CreateLobbyResponse(LobbyName lobbyName, Lobby lobby) {
        super(lobbyName);
        this.lobby = lobby;
    }

    /**
     * Gets the created lobby
     *
     * @return The created lobby
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    public Lobby getLobby() {
        return lobby;
    }
}
