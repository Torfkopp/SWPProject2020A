package de.uol.swp.common.lobby.response;

import java.util.Map;

public class UpdateBankInventoryResponse extends AbstractLobbyResponse{

    private final Map<String, Integer> resourceMap;

    /**
     * Constructor
     * @param lobbyName The name of the Lobby which this Response is directed to
     * @param resourceMap
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
