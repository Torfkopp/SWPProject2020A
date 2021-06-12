package de.uol.swp.client.changeSettings.event;

/**
 * Event used to show a SetVolumeError alert
 * <p>
 * In order to show a SetVolumeError alert using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 *
 * @author Alwin Bossert
 * @see de.uol.swp.client.SceneManager
 * @since 2021-06-05
 */
public class SetVolumeErrorEvent {

    private final String message;

    /**
     * Constructor
     *
     * @param message Message containing the cause of the Error
     */
    public SetVolumeErrorEvent(String message) {
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