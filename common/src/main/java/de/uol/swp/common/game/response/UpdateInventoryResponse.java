package de.uol.swp.common.game.response;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.resourceThingies.developmentCard.DevelopmentCardListMap;
import de.uol.swp.common.game.resourceThingies.resource.resourceListMap.MutableResourceListMap;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.UserOrDummy;

import java.util.List;
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
    private final MutableResourceListMap resourceMap;
    private final DevelopmentCardListMap developmentCardMap;

    public DevelopmentCardListMap getDevelopmentCardMap() {
        return developmentCardMap;
    }

    /**
     * Constructor
     *
     * @param user                The user wanting to update the inventory
     * @param lobbyName           The lobby for which the update is supposed to happen in
     * @param developmentCardList List of the Resources in the user's inventory, containing Maps
     *                            containing the following for each type of Resource:<p>
     *                            {@literal {"amount": <Integer>, "resource", <Resource>}}
     * @param resourceList        List of the Development Cards in the user's inventory, containing
     *                            Maps containing the following for each type of Development Card:<p>
     *                            {@literal {"amount": <Integer>, "card": "game.resources.cards.<Dev Card key>"}}
     *
     * @author Phillip-André Suhr
     * @since 2021-04-17
     */
    public UpdateInventoryResponse(UserOrDummy user, LobbyName lobbyName, MutableResourceListMap resourceMap, DevelopmentCardListMap developmentCardMap) {
        super(lobbyName);
        this.user = user;
        this.resourceMap = resourceMap;
        this.developmentCardMap = developmentCardMap;
    }

    /**
     * Gets the list of Resource maps for the MapValueFactory
     *
     * @return List of Maps for MapValueFactory
     *
     * @author Phillip-André Suhr
     * @since 2021-04-17
     */
    public MutableResourceListMap getResourceMap() {
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
