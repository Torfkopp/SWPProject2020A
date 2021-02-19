package de.uol.swp.client.Trade.Event;

import de.uol.swp.common.user.User;

/**
 * Event used to show the window for the trading with the bank
 * <p>
 * In order to show the previous window using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 *
 * @author Alwin Bossert
 * @author Alwin Bossert
 * @see de.uol.swp.client.SceneManager
 * @since 2021-02-19
 */
public class ShowTradeWithBankViewEvent {

    private final User user;

    /**
     * Constructor
     *
     * @param user User that wants to trade with the bank
     */
    public ShowTradeWithBankViewEvent(User user) {
        this.user = user;
    }

    /**
     * Gets the User object
     *
     * @return User object of the event
     */
    public User getUser() {
        return user;
    }
}
