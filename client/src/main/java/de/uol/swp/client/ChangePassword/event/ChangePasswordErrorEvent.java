package de.uol.swp.client.ChangePassword.event;

/**
 * Event used to show a ChangePasswordError alert
 * <p>
 * In order to show a ChangePasswordError alert using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 *
 * @author Eric Vuong
 * @see de.uol.swp.client.SceneManager
 * @since 2020-11-25
 */
public class ChangePasswordErrorEvent {

    private final String message;

    /**
     * Constructor
     *
     * @since 2020-11-25
     */
    public ChangePasswordErrorEvent(String message) {
        this.message = message;
    }

    /**
     * Gets the error message
     *
     * @since 2020-11-25
     */
    public String getMessage() {
        return message;
    }
}
