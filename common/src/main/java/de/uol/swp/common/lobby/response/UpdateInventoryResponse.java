package de.uol.swp.common.lobby.response;

import de.uol.swp.common.user.User;

import java.util.Map;

/**
 * This Response has up-to-date info about what the inventory of a specified player contains
 *
 * @author Sven Ahrens
 * @author Finn Haase
 * @since 2021-01-25
 */
public class UpdateInventoryResponse extends AbstractLobbyResponse {

    private final User user;
    private final Map<String, Integer> resourceMap;
    private final Map<String, Boolean> armyAndRoadMap;

    /**
     * Constructor
     *
     * @param user           The user wanting to update the inventory
     * @param lobbyName      The lobby for which the update is supposed to happen in
     * @param resourceMap    The Map containing the name of a resource as key and the amount as value
     * @param armyAndRoadMap The Map containing "Largest Army" and "Longest Road" with the appropriate boolean as the value
     */
    public UpdateInventoryResponse(User user, String lobbyName, Map<String, Integer> resourceMap,
                                   Map<String, Boolean> armyAndRoadMap) {
        super(lobbyName);
        this.user = user;
        this.resourceMap = resourceMap;
        this.armyAndRoadMap = armyAndRoadMap;
    }

    /**
     * Gets the army and road map, containing mappings of "Largest Army" and
     * "Longest Road" to their appropriate boolean values.
     *
     * @return The army and road map
     */
    public Map<String, Boolean> getArmyAndRoadMap() {
        return armyAndRoadMap;
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
