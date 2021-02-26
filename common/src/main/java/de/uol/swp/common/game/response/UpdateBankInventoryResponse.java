package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.response.AbstractLobbyResponse;

import java.util.Map;

/**
 * This Response has up-to-date info about what the inventory of a specified bank contains
 *
 * @author Maximilian Lindner
 * @author Alwin Bossert
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-02-21
 */
public class UpdateBankInventoryResponse extends AbstractLobbyResponse {

    private final Map<String, Integer> resourceMap;

    /**
     * Constructor
     *
     * @param lobbyName   The name of the Lobby which this Response is directed to
     * @param resourceMap The Inventory of a user
     */
    public UpdateBankInventoryResponse(String lobbyName, Map<String, Integer> resourceMap) {
        super(lobbyName);
        this.resourceMap = resourceMap;
    }

    /**
     * Gets the resource map, containing mappings of resource name to resource amount.
     * <p>
     * E.g. "Bricks", 1
     *
     * @return The resource map
     */
    public Map<String, Integer> getResourceMap() {
        return resourceMap;
    }
}
