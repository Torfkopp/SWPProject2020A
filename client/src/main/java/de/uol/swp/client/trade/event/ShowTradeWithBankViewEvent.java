package de.uol.swp.client.trade.event;

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
    private final String lobbyName;

    /**
     * Constructor
     *
     * @param user      User that wants to trade with the bank
     * @param lobbyName
     */
    public ShowTradeWithBankViewEvent(User user, String lobbyName) {
        this.user = user;
        this.lobbyName = lobbyName;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the User who wants to trade with the Bank
     *
     * @return User object of the event
     */
    public User getUser() {
        return user;
    }
}
