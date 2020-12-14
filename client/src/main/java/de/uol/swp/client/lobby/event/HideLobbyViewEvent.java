package de.uol.swp.client.lobby.event;

/**
 * Event used to hide the lobby window
 * <p>
 * In order to hide the lobby window using this event, post an instance of it
 * onto the eventBus the SceneManager is subscribed to.
 *
 * @author Mario
 * @see de.uol.swp.client.SceneManager
 * @since 2020-12-14
 */
public class HideLobbyViewEvent {

    private String lobbyName;

    /**
     * Default Constructor
     *
     * @since 2020-12-14
     */
    public HideLobbyViewEvent() {
    }

    /**
     * Constructor
     *
     * @param lobbyName The name of the lobby
     * @since 2020-12-14
     */
    public HideLobbyViewEvent(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Getter of the lobbyName
     *
     * @return Name of the lobby
     * @since 2020-12-14
     */
    public String getLobbyName() {
        return lobbyName;
    }
}
