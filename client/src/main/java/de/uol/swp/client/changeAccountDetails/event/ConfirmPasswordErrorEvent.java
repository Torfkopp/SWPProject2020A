package de.uol.swp.client.changeAccountDetails.event;

/**
 * Event used to show a ConfirmPasswordError alert
 * <p>
 * In order to show a ConfirmPasswordError alert using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 *
 * @author Eric Vuong
 * @author Alwin Bossert
 * @see de.uol.swp.client.SceneManager
 * @since 2021-03-16
 */
public class ConfirmPasswordErrorEvent {

    private final String message;

    /**
     * Constructor
     *
     * @since 2021-03-16
     */
    public ConfirmPasswordErrorEvent(String message) {
        this.message = message;
    }

    /**
     * Gets the error message
     *
     * @since 2021-03-16
     */
    public String getMessage() {
        return message;
    }
}
