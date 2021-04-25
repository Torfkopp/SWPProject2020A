package de.uol.swp.common.game.response;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.resourceThingies.resource.resourceListMap.MutableResourceListMap;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.UserOrDummy;

import java.util.List;
import java.util.Map;

/**
 * This Response has up-to-date info about the resources in the inventory
 * of the player who opened the trade window and the amount of resource cards the other
 * user has.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-02-23
 */
public class InventoryForTradeWithUserResponse extends AbstractLobbyResponse {

    private final UserOrDummy user;
    private final MutableResourceListMap resourceMap;
    private final int tradingUsersInventorySize;
    private final UserOrDummy tradingUser;

    /**
     * Constructor
     *
     * @param user                      The User wanting to create a trade offer
     * @param lobbyName                 The Lobby in which the trade is happening
     * @param resourceList              List of the Resources in the offering User's inventory, containing Maps
     *                                  containing the following for each type of Resource:<p>
     *                                  {@literal {"amount": <Integer>, "resource", <Resource>}}
     * @param tradingUsersInventorySize Amount of resource cards the other User has
     * @param tradingUser               The User to whom an offer is going to be made
     */
    public InventoryForTradeWithUserResponse(UserOrDummy user, LobbyName lobbyName, MutableResourceListMap resourceMap,
                                             int tradingUsersInventorySize, UserOrDummy tradingUser) {
        super(lobbyName);
        this.user = user;
        this.resourceList = resourceList;
        this.tradingUsersInventorySize = tradingUsersInventorySize;
        this.tradingUser = tradingUser;
    }

    /**
     * Gets the list of Resource maps for the MapValueFactory
     *
     * @return List of Maps for MapValueFactory
     *
     * @author Phillip-André Suhr
     * @since 2021-04-19
     */
    public MutableResourceListMap getResourceMap() {
        return resourceMap;
    }

    /**
     * Gets the User to whom a trade offer is being made
     *
     * @return The User being traded with
     */
    public UserOrDummy getTradingUser() {
        return tradingUser;
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
     * Gets the User wanting to create a trade offer
     *
     * @return The User wanting to create a trade offer
     */
    public UserOrDummy getUser() {
        return user;
    }
}
