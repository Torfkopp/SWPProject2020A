package de.uol.swp.client.lobby.event;

/**
 * Event used to show a window of the password confirmation of a specified lobby
 * <p>
 * In order to show the confirmation window using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 *
 * @author Alwin Bossert
 * @see de.uol.swp.client.SceneManager
 * @since 2021-04-22
 */
public class ShowLobbyWithPasswordViewEvent {

    private final String name;

    /**
     * Constructor
     *
     * @param name Name containing the lobby's name
     */
    public ShowLobbyWithPasswordViewEvent(String name) {this.name = name;}

    /**
     * Gets the lobby's name
     *
     * @return A String containing the lobby's name
     */
    public String getName() {
        return name;
    }
}
