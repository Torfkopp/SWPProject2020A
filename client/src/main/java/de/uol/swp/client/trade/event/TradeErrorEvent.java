package de.uol.swp.client.trade.event;

/**
 * Event used to show a Trade alert
 * <p>
 * In order to show a Trade alert using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.client.SceneManager
 * @since 2021-02-25
 */
public class TradeErrorEvent {

    private final String message;

    /**
     * Constructor
     *
     * @param message Message containing the cause of the Error
     */
    public TradeErrorEvent(String message) {
        this.message = message;
    }

    /**
     * Gets the error message
     *
     * @return A String containing the error message
     */
    public String getMessage() {
        return message;
    }
}
