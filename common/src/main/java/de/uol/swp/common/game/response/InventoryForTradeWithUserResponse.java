package de.uol.swp.common.game.response;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.UserOrDummy;

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
    private final ResourceList resourceList;
    private final int tradingUsersInventorySize;
    private final UserOrDummy tradingUser;
    private final boolean counterOffer;
    private final int maxTradeDiff;

    /**
     * Constructor
     *
     * @param user                      The User wanting to create a trade offer
     * @param lobbyName                 The Lobby in which the trade is happening
     * @param resourceList              The list of resources of the offering user
     * @param tradingUsersInventorySize Amount of resource cards the other User has
     * @param tradingUser               The User to whom an offer is going to be made
     * @param counterOffer              Whether the offer is a counter offer or not
     * @param maxTradeDiff              Maximum Resource difference for a trade
     */
    public InventoryForTradeWithUserResponse(UserOrDummy user, LobbyName lobbyName, ResourceList resourceList,
                                             int tradingUsersInventorySize, UserOrDummy tradingUser,
                                             boolean counterOffer, int maxTradeDiff) {
        super(lobbyName);
        this.user = user;
        this.resourceList = resourceList;
        this.tradingUsersInventorySize = tradingUsersInventorySize;
        this.tradingUser = tradingUser;
        this.counterOffer = counterOffer;
        this.maxTradeDiff = maxTradeDiff;
    }

    /**
     * Gets the maximum Trade Difference for a lobby
     *
     * @return Difference of acceptable Ressource
     *
     * @author Aldin Dervisi
     * @since 2021-06-08
     */
    public int getMaxTradeDiff() {
        return maxTradeDiff;
    }

    /**
     * Gets the resource list
     *
     * @return List of Maps for MapValueFactory
     *
     * @author Phillip-André Suhr
     * @since 2021-04-19
     */
    public ResourceList getResourceMap() {
        return resourceList.create();
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

    /**
     * Gets the counter offer status
     *
     * @return Whether the offer is a counter offer or not
     */
    public boolean isCounterOffer() {
        return counterOffer;
    }
}
