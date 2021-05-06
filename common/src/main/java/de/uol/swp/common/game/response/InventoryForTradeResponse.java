package de.uol.swp.common.game.response;

import de.uol.swp.common.game.map.hexes.IHarborHex;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.lobby.LobbyName;
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
    private final ResourceList resourceList;
    private final List<IHarborHex.HarborResource> harborResourceList;

    /**
     * Constructor
     *
     * @param user               The user wanting to update the inventory
     * @param lobbyName          The lobby for which the update is supposed to happen in
     * @param resourceList       The resource list
     * @param harborResourceList The List containing all the types of harbors the Player owns
     */
    public InventoryForTradeResponse(UserOrDummy user, LobbyName lobbyName, ResourceList resourceList,
                                     List<IHarborHex.HarborResource> harborResourceList) {
        super(lobbyName);
        this.user = user;
        this.resourceList = resourceList;
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
     * Gets the resource list.
     * <p>
     * E.g. "Bricks", 1
     *
     * @return The resource list
     */
    public ResourceList getResourceList() {
        return resourceList;
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
