package de.uol.swp.client.trade.event;

import de.uol.swp.common.lobby.LobbyName;

/**
 * Event used to trigger the updating of the TradeWithUserPresenter
 * <p>
 * In order to give a TradeWithUserPresenter its name and User who wants to trade, post an
 * instance of it onto the EventBus the TradeWithUserPresenter is subscribed to.
 * All the methods are available in the LobbyUpdateEvent
 *
 * @author Maximilian Lindner
 * @author Alwin Bossert
 * @see de.uol.swp.client.trade.TradeWithUserPresenter
 * @see de.uol.swp.client.trade.event.TradeWithUserUpdateEvent
 * @since 2021-02-20
 */
public class TradeWithUserUpdateEvent {

    private final LobbyName lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby of the trade
     */
    public TradeWithUserUpdateEvent(LobbyName lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Gets the name of the lobby
     *
     * @return The lobbyName
     */
    public LobbyName getLobbyName() {
        return lobbyName;
    }
}
