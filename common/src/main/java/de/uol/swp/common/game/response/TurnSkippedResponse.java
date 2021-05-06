package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;

/**
 * Response sent by the server to tell a client that their turn was
 * forcibly skipped
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @since 2021-03-07
 */
public class TurnSkippedResponse extends AbstractLobbyResponse {

    /**
     * Constructor
     *
     * @param lobbyName The lobby in which the game is taking place
     */
    public TurnSkippedResponse(LobbyName lobbyName) {
        super(lobbyName);
    }
}
