package de.uol.swp.common.game.response;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.DevelopmentCardListMap;
import de.uol.swp.common.game.ResourceListMap;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.UserOrDummy;

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
    private final ResourceListMap resourceMap;
    private final DevelopmentCardListMap developmentCardMap;

    public DevelopmentCardListMap getDevelopmentCardMap() {
        return developmentCardMap;
    }

    /**
     * Constructor
     *  @param user           The user wanting to update the inventory
     * @param lobbyName      The lobby for which the update is supposed to happen in
     * @param resourceMap    The Map containing the name of a resource as key and the amount as value
     */
    public UpdateInventoryResponse(UserOrDummy user, LobbyName lobbyName, ResourceListMap resourceMap, DevelopmentCardListMap developmentCardMap) {
        super(lobbyName);
        this.user = user;
        this.resourceMap = resourceMap;
        this.developmentCardMap = developmentCardMap;
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
     * Gets the user whose inventory is being updated
     *
     * @return The User whose inventory is being updated
     */
    public UserOrDummy getUser() {
        return user;
    }
}
