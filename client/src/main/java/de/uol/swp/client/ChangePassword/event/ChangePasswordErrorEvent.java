package de.uol.swp.client.ChangePassword.event;

/**
 * Event used to show the ChangePasswordError alert
 *
 * In order to show the ChangePasswordError alert using this event, post an instance of it
 * onto the eventBus the SceneManager is subscribed to.
 *
 */

public class ChangePasswordErrorEvent {
    private final String message;

    /**
     * Constructor
     *
     */
    public ChangePasswordErrorEvent(String message) {
        this.message = message;
    }

    /**
     * Gets the error message
     *
     */
    public String getMessage() {
        return message;
    }
}
