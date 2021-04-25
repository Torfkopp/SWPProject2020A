package de.uol.swp.common.game.response;

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
    private final List<Map<String, Object>> resourceList;
    private final List<Map<String, Object>> offeredResources;
    private final List<Map<String, Object>> demandedResources;

    /**
     * Constructor
     *
     * @param offeringUser      The offering User
     * @param resourceList      The inventory of the responding user
     * @param offeredResources  The offered resources
     * @param demandedResources The demanded resources
     * @param lobbyName         The name of the lobby
     */
    public TradeWithUserOfferResponse(UserOrDummy offeringUser, List<Map<String, Object>> resourceList,
                                      List<Map<String, Object>> offeredResources,
                                      List<Map<String, Object>> demandedResources, String lobbyName) {
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
    public List<Map<String, Object>> getOfferedResources() {
        return offeredResources;
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
    public List<Map<String, Object>> getResourceList() {
        return resourceList;
    }
}
