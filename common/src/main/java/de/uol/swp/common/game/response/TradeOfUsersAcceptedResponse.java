package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;

/**
 * This Response contains a lobby where a successful
 * trade between 2 user´s happened.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-02-25
 */
public class TradeOfUsersAcceptedResponse extends AbstractLobbyResponse {

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     */
    public TradeOfUsersAcceptedResponse(LobbyName lobbyName) {
        super(lobbyName);
    }
}
