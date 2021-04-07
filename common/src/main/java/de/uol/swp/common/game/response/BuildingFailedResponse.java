package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.response.AbstractLobbyResponse;

public class BuildingFailedResponse extends AbstractLobbyResponse {
    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby which this Response is directed to
     */
    public BuildingFailedResponse(String lobbyName) {
        super(lobbyName);
    }
}
