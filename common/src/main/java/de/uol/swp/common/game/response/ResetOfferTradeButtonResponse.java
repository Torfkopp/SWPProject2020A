package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;

/**
 * This Response is used to reset the offer trade
 * with user button in a game.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-02-25
 */
public class ResetOfferTradeButtonResponse extends AbstractLobbyResponse {

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     */
    public ResetOfferTradeButtonResponse(LobbyName lobbyName) {
        super(lobbyName);
    }
}
