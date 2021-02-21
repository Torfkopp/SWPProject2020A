package de.uol.swp.client.trade.event;

import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
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
public class TradeUpdateEvent extends LobbyUpdateEvent {

    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby to update
     * @param user      The name of the User who caused this Event (Creator or Joining User)
     */
    public TradeUpdateEvent(String lobbyName, User user) {
        super(lobbyName, user);
    }
}
