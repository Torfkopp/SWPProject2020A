package de.uol.swp.client.trade.event;

import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.common.user.User;

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
public class TradeWithUserUpdateEvent extends LobbyUpdateEvent {

    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby of the trade
     * @param user      The name of the User who wants to trade
     */
    public TradeWithUserUpdateEvent(String lobbyName, User user) {
        super(lobbyName, user);
    }
}