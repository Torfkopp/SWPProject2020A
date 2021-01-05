package de.uol.swp.client.lobby.event;

/**
 * Event used to show the LobbyError alert
 * <p>
 * In order to show the LobbyError alert using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 *
 * @author Marvin
 * @author Aldin
 * @see de.uol.swp.client.SceneManager
 * @since 2020-12-18
 */
public class LobbyErrorEvent {
    private final String message;

    /**
     * Constructor
     *
     * @param message Message containing the cause of the Error
     * @since 2020-12-18
     */
    public LobbyErrorEvent(String message) {
        this.message = message;
    }

    /**
     * Gets the error message
     *
     * @return A String containing the error message
     * @since 2020-12-18
     */
    public String getMessage() {
        return message;
    }
}
