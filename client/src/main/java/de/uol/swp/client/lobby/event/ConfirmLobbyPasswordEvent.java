package de.uol.swp.client.lobby.event;

public class ConfirmLobbyPasswordEvent {

    private final String lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby
     */
    public ConfirmLobbyPasswordEvent(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Gets the name of the lobby
     *
     * @return The lobbyName
     */
    public String getLobbyName() {
        return lobbyName;
    }
}
