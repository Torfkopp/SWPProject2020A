package de.uol.swp.client.lobby.event;

/**
 * Event used to show a SetMoveTimeError alert
 * <p>
 * In order to show a SetMoveTimeError alert using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 *
 * @author Alwin Bossert
 * @see de.uol.swp.client.SceneManager
 * @since 2021-05-03
 */
public class SetMoveTimeErrorEvent {

    private final String message;

    /**
     * Constructor
     *
     * @since 2021-05-03
     */
    public SetMoveTimeErrorEvent(String message) {
        this.message = message;
    }

    /**
     * Gets the error message
     *
     * @since 2021-05-03
     */
    public String getMessage() {
        return message;
    }
}
