package de.uol.swp.client.lobby.event;

import de.uol.swp.common.lobby.ISimpleLobby;

/**
 * Event used to show a window of a specified lobby
 * <p>
 * In order to show the lobby window using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 *
 * @author Mario Fokken
 * @see de.uol.swp.client.SceneManager
 * @since 2020-11-21
 */
public class ShowLobbyViewEvent {

    private final ISimpleLobby lobby;

    /**
     * Constructor
     *
     * @param lobby Name containing the lobby's name
     */
    public ShowLobbyViewEvent(ISimpleLobby lobby) {
        this.lobby = lobby;
    }

    /**
     * Gets the lobby's name
     *
     * @return A String containing the lobby's name
     */
    public ISimpleLobby getLobby() {
        return lobby;
    }
}
