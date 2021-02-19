package de.uol.swp.client.Trade.Event;

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

    private final String name;

    /**
     * Constructor
     *
     * @param name Name containing the users name
     */
    public ShowTradeWithBankViewEvent(String name) {
        this.name = name;
    }

    /**
     * Gets the user´s Name
     *
     * @return A String containing the user´s Name
     */
    public String getName() {
        return name;
    }
}
