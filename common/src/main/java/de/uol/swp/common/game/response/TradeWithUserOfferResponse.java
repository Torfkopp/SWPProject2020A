package de.uol.swp.common.game.response;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.resourceThingies.resource.resourceListMap.MutableResourceListMap;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.UserOrDummy;

import java.util.List;
import java.util.Map;

/**
 * This response is sent when a user offers another user
 * a trade of resources. This response contains the necessary
 * information about the trade.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-02-25
 */
public class TradeWithUserOfferResponse extends AbstractLobbyResponse {

    private final UserOrDummy offeringUser;
    private final MutableResourceListMap resourceMap;
    private final MutableResourceListMap offeringResourceMap;
    private final MutableResourceListMap respondingResourceMap;

    /**
     * Constructor
     *
     * @param offeringUser      The offering User
     * @param resourceList      The inventory of the responding user
     * @param offeredResources  The offered resources
     * @param demandedResources The demanded resources
     * @param lobbyName         The name of the lobby
     */
    public TradeWithUserOfferResponse(UserOrDummy offeringUser,
                                      MutableResourceListMap resourceMap, MutableResourceListMap offeringResourceMap,
                                      MutableResourceListMap respondingResourceMap, LobbyName lobbyName) {
        super(lobbyName);
        this.resourceList = resourceList;
        this.offeredResources = offeredResources;
        this.demandedResources = demandedResources;
        this.offeringUser = offeringUser;
    }

    /**
     * Gets the demanded resources
     *
     * @return Map of demanded resources
     */
    public List<Map<String, Object>> getDemandedResources() {
        return demandedResources;
    }

    /**
     * Gets the offered resources
     *
     * @return Map of offered resources
     */
    public MutableResourceListMap getOfferingResourceMap() {
        return offeringResourceMap;
    }

    /**
     * Gets the offering User
     *
     * @return A User-Object of the offering User
     */
    public UserOrDummy getOfferingUser() {
        return offeringUser;
    }

    /**
     * Gets the inventory of the responding User
     *
     * @return Map of the inventory of the responding User
     */
    public MutableResourceListMap getResourceMap() {
        return resourceMap;
    }

    /**
     * Gets the demanded resources
     *
     * @return Map of demanded resources
     */
    public MutableResourceListMap getRespondingResourceMap() {
        return respondingResourceMap;
    }
}
