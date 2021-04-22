package de.uol.swp.common.game.response;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.ResourceListMap;
import de.uol.swp.common.game.map.Hexes.IHarborHex;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.UserOrDummy;

import java.util.List;

/**
 * This Response has up-to-date info about what the inventory of a specified player contains
 *
 * @author Maximilian Lindner
 * @author Alwin Bossert
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-02-21
 */
public class InventoryForTradeResponse extends AbstractLobbyResponse {

    private final UserOrDummy user;
    private final ResourceListMap resourceMap;
    private final List<IHarborHex.HarborResource> harborResourceList;

    /**
     * Constructor
     *
     * @param user               The user wanting to update the inventory
     * @param lobbyName          The lobby for which the update is supposed to happen in
     * @param resourceMap        The Map containing the name of a resource as key and the amount as value
     * @param harborResourceList The List containing all the harbors of the Player
     */
    public InventoryForTradeResponse(UserOrDummy user, LobbyName lobbyName, ResourceListMap resourceMap,
                                     List<IHarborHex.HarborResource> harborResourceList) {
        super(lobbyName);
        this.user = user;
        this.resourceMap = resourceMap;
        this.harborResourceList = harborResourceList;
    }

    /**
     * Gets the List of the Harbors of the User
     *
     * @return List of Harbors
     *
     * @author Steven Luong
     * @author Maximilian Lindner
     * @since 2021-04-07
     */
    public List<IHarborHex.HarborResource> getHarborResourceList() {
        return harborResourceList;
    }

    /**
     * Gets the resource map, containing mappings of resource name to resource amount.
     * <p>
     * E.g. "Bricks", 1
     *
     * @return The resource map
     */
    public ResourceListMap getResourceMap() {
        return resourceMap;
    }

    /**
     * Gets the user who wants to get his inventory
     *
     * @return The User who wants to get his inventory
     */
    public UserOrDummy getUser() {
        return user;
    }
}
