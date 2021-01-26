package de.uol.swp.common.lobby.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

import java.util.Map;

public class UpdateInventoryResponse extends AbstractResponseMessage {

    private final User user;
    private final String lobbyName;
    private final Map<String, Integer> resourceMap;
    private final Map<String, Boolean> armyAndRoadMap;

    /**
     * Constructor
     *
     * @param user the user wanting to update the inventory
     * @param lobbyName the lobby for which the update is supposed to happen in
     * @param resourceMap the resourceMap used to update the clients Inventory
     * @param armyAndRoadMap theArmyAndResourceMap used to update the clients inventory
     * @author Sven Ahrens
     * @author Finn Haase
     * @since 2021-01-25
     **/
    public UpdateInventoryResponse(User user, String lobbyName, Map<String, Integer> resourceMap, Map<String, Boolean> armyAndRoadMap) {
        this.user = user;
        this.lobbyName = lobbyName;
        this.resourceMap = resourceMap;
        this.armyAndRoadMap = armyAndRoadMap;
    }

    public User getPlayer() {
        return user;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public Map<String, Integer> getResourceMap() {
        return resourceMap;
    }

    public Map<String, Boolean> getArmyAndRoadMap() {
        return armyAndRoadMap;
    }
}
