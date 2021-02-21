package de.uol.swp.common.lobby.response;

import de.uol.swp.common.user.User;

import java.util.Map;

/**
 * This Response has up-to-date info about what the inventory of a specified player contains
 *
 * @author Sven Ahrens
 * @author Finn Haase
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-01-25
 */
public class InventoryForTradeResponse extends AbstractLobbyResponse {

    private final User user;
    private final Map<String, Integer> resourceMap;

    /**
     * Constructor
     *
     * @param user        The user wanting to update the inventory
     * @param lobbyName   The lobby for which the update is supposed to happen in
     * @param resourceMap The Map containing the name of a resource as key and the amount as value
     */
    public InventoryForTradeResponse(User user, String lobbyName, Map<String, Integer> resourceMap) {
        super(lobbyName);
        this.user = user;
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

    /**
     * Gets the user whose inventory is being updated
     *
     * @return The User whose inventory is being updated
     */
    public User getUser() {
        return user;
    }
}
