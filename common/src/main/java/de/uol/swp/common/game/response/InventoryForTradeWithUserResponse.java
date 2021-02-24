package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.User;

import java.util.Map;

/**
 * This Response has up-to-date info about the inventory's resources of the
 * player who opened the trade window and the amount of resource cards the other
 * user has.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-02-23
 */
public class InventoryForTradeWithUserResponse extends AbstractLobbyResponse {

    private final User user;
    private final Map<String, Integer> resourceMap;
    private final int tradingUsersInventorySize;
    private final String tradingUserName;

    /**
     * Constructor
     * @param user                     The user wanting to update the inventory
     * @param lobbyName                 The lobby for which the update is supposed to happen in
     * @param resourceMap               The Map containing the name of a resource as key and the amount as value
     * @param tradingUsersInventorySize Amount of resource cards the other user has
     * @param tradingUserName           The trading Users name
     */
    public InventoryForTradeWithUserResponse(User user, String lobbyName, Map<String, Integer> resourceMap,
                                             int tradingUsersInventorySize, String tradingUserName) {
        super(lobbyName);
        this.user = user;
        this.resourceMap = resourceMap;
        this.tradingUsersInventorySize = tradingUsersInventorySize;
        this.tradingUserName = tradingUserName;
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
     * Gets the amount of resource cards the other user has.
     *
     * @return The amount of resource cards
     */
    public int getTradingUsersInventorySize() {
        return tradingUsersInventorySize;
    }

    /**
     * Gets the user who wants to get his inventory
     *
     * @return The User who wants to get his inventory
     */
    public User getUser() {
        return user;
    }

    public String getTradingUserName() {
        return tradingUserName;
    }
}
