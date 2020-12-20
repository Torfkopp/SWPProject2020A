package de.uol.swp.client.lobby.event;

/**
 * Event used to show the lobby window
 * <p>
 * In order to show the lobby window using this event, post an instance of it
 * onto the eventBus the SceneManager is subscribed to.
 *
 * @author Mario
 * @see de.uol.swp.client.SceneManager
 * @since 2020-11-21
 */
public class ShowLobbyViewEvent {
    private final String name;

    /**
     * Constructor
     *
     * @param name Name containing the Lobby's name
     */
    public ShowLobbyViewEvent(String name) {
        this.name = name;
    }

    /**
     * Gets the name
     *
     * @return A String containing the lobby's name
     */
    public String getName() {
        return name;
    }
}
