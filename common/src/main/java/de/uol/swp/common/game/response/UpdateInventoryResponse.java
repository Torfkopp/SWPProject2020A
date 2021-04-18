package de.uol.swp.common.game.response;

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
    private final List<Map<String, Object>> resourceList;
    private final List<Map<String, Object>> developmentCardList;

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
    public UpdateInventoryResponse(UserOrDummy user, String lobbyName, List<Map<String, Object>> developmentCardList,
                                   List<Map<String, Object>> resourceList) {
        super(lobbyName);
        this.user = user;
        this.resourceList = resourceList;
        this.developmentCardList = developmentCardList;
    }

    /**
     * Gets the list of Development Card maps for the MapValueFactory
     *
     * @return List of Maps for MapValueFactory
     *
     * @author Phillip-André Suhr
     * @since 2021-04-17
     */
    public List<Map<String, Object>> getDevelopmentCardList() {
        return developmentCardList;
    }

    /**
     * Gets the list of Resource maps for the MapValueFactory
     *
     * @return List of Maps for MapValueFactory
     *
     * @author Phillip-André Suhr
     * @since 2021-04-17
     */
    public List<Map<String, Object>> getResourceList() {
        return resourceList;
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
