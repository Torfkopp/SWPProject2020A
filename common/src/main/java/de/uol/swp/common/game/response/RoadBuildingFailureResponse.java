package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.response.AbstractLobbyResponse;

/**
 * Response sent when the RoadBuildingCard could not be played,
 * because the chosen roads are not buildable.
 *
 * @author Mario Fokken
 * @since 2021-04-16
 */
public class RoadBuildingFailureResponse extends AbstractLobbyResponse {

    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby which this Response is directed to
     */
    public RoadBuildingFailureResponse(String lobbyName) {
        super(lobbyName);
    }
}
