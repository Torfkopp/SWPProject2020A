package de.uol.swp.client.trade.event;

import de.uol.swp.common.user.User;

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

    private final String lobbyName;
    private final User user;

    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby of the trade
     * @param user      The name of the User who wants to trade
     */
    public TradeUpdateEvent(String lobbyName, User user) {
        this.lobbyName = lobbyName;
        this.user = user;
    }

    /**
     * Gets the name of the lobby
     *
     * @return The lobbyName
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the user
     *
     * @return The User
     */
    public User getUser() {
        return user;
    }
}