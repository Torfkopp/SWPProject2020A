package de.uol.swp.client.trade.event;

import de.uol.swp.common.LobbyName;

/**
 * Event used to trigger the updating of the TradeWithBankPresenter
 * <p>
 * In order to give a TradeWithBankPresenter its name and User who wants to trade, post an
 * instance of it onto the EventBus the TradeWithBankPresenter is subscribed to.
 * All the methods are available in the LobbyUpdateEvent
 *
 * @author Maximilian Lindner
 * @author Alwin Bossert
 * @see de.uol.swp.client.trade.TradeWithBankPresenter
 * @see de.uol.swp.client.lobby.event.LobbyUpdateEvent
 * @since 2021-02-20
 */
public class TradeUpdateEvent {

    private final LobbyName lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby of the trade
     */
    public TradeUpdateEvent(LobbyName lobbyName) {
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
