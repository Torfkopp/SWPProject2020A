package de.uol.swp.client.lobby.event;

public class ShowLobbyWithPasswordViewEvent {

    private final String name;

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
