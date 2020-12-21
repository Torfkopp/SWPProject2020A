package de.uol.swp.client.lobby.event;

public class LobbyReadyEvent {

    private final String name;

    public LobbyReadyEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
