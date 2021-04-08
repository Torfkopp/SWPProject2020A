package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.response.AbstractLobbyResponse;

public class BuildingFailedResponse extends AbstractLobbyResponse {

    private final Reason reason;

    public enum Reason {
        NOT_ENOUGH_RESOURCES,
        CANT_BUILD_HERE
    }

    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby which this Response is directed to
     */
    public BuildingFailedResponse(String lobbyName, Reason reason) {
        super(lobbyName);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
