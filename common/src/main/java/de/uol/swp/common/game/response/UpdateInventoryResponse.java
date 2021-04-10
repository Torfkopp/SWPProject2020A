package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.UserOrDummy;

import java.util.Map;

/**
 * This Response has up-to-date info about what the inventory of a specified player contains
 *
 * @author Sven Ahrens
 * @author Finn Haase
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-01-25
 */
public class UpdateInventoryResponse extends AbstractLobbyResponse {

    private final UserOrDummy user;
    private final Map<String, Integer> resourceMap;

    /**
     * Constructor
     *
     * @param user           The user wanting to update the inventory
     * @param lobbyName      The lobby for which the update is supposed to happen in
     * @param resourceMap    The Map containing the name of a resource as key and the amount as value
     * @param armyAndRoadMap The Map containing "Largest Army" and "Longest Road" with the appropriate boolean as the value
     */
    public UpdateInventoryResponse(UserOrDummy user, String lobbyName, Map<String, Integer> resourceMap) {
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
    public UserOrDummy getUser() {
        return user;
    }
}
