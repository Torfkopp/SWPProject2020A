package de.uol.swp.client.trade.event;

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
 * @see de.uol.swp.client.lobby.event.LobbyUpdateEvent
 * @since 2021-02-20
 */
public class TradeWithUserUpdateEvent {

    private final String lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby of the trade
     */
    public TradeWithUserUpdateEvent(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Gets the name of the lobby
     *
     * @return The lobbyName
     */
    public String getLobbyName() {
        return lobbyName;
    }
}
