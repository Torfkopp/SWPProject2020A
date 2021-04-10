package de.uol.swp.common.game.response;

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
        NOT_ENOUGH_RESOURCES,
        CANT_BUILD_HERE,
        ALREADY_BUILT_HERE,
        NOTHING_HERE,
        BAD_GROUND
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

    /**
     * Gets the reason for failed building attempt
     *
     * @return The reason
     */
    public Reason getReason() {
        return reason;
    }
}
