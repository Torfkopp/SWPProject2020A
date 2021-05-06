package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;

/**
 * Sent to the client to indicate that a building attempt failed
 *
 * @author Aldin Dervisi
 * @author Temmo Junkhoff
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-04-07
 */
public class BuildingFailedResponse extends AbstractLobbyResponse {

    private final Reason reason;

    /**
     * An enum of possible reasons
     */
    public enum Reason {
        ALREADY_BUILT_HERE,
        BAD_GROUND,
        CANT_BUILD_HERE,
        NOTHING_HERE,
        NOT_ENOUGH_RESOURCES,
        NOT_A_REAL_ROAD,
        NOT_THE_RIGHT_TIME
    }

    /**
     * Constructor
     *
     * @param lobbyName The name of the Lobby which this Response is directed to
     */
    public BuildingFailedResponse(LobbyName lobbyName, Reason reason) {
        super(lobbyName);
        this.reason = reason;
    }

    /**
     * Gets the reason for failed building attempt
     *
     * @return The reason
     */
    public Reason getReason() {
        return reason;
    }
}
