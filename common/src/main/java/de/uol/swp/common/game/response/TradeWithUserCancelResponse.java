package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.Actor;

/**
 * This Response is sent when a trade between 2 users got cancelled.
 *
 * @author Maximilian Lindner
 * @author Aldin Dervisi
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-03-19
 */
public class TradeWithUserCancelResponse extends AbstractLobbyResponse {

    private final Actor activePlayer;

    /**
     * Constructor.
     *
     * @param lobbyName    The name of the Lobby
     * @param activePlayer The active player in the actual game
     */
    public TradeWithUserCancelResponse(LobbyName lobbyName, Actor activePlayer) {
        super(lobbyName);
        this.activePlayer = activePlayer;
    }

    /**
     * Gets the active player.
     *
     * @return The active player
     */
    public Actor getActivePlayer() {
        return activePlayer;
    }
}
