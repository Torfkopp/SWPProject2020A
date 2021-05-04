package de.uol.swp.common.game.response;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.UserOrDummy;

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
    private final ResourceList resourceList;
    private final ResourceList offeredResources;
    private final ResourceList demandedResources;

    /**
     * Constructor
     *
     * @param offeringUser      The offering User
     * @param resourceList      The inventory of the responding user
     * @param offeredResources  The offered resources
     * @param demandedResources The demanded resources
     * @param lobbyName         The name of the lobby
     */
    public TradeWithUserOfferResponse(UserOrDummy offeringUser, ResourceList resourceList,
                                      ResourceList offeredResources, ResourceList demandedResources,
                                      LobbyName lobbyName) {
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
    public ResourceList getDemandedResources() {
        return demandedResources;
    }

    /**
     * Gets the offered resources
     *
     * @return Map of offered resources
     */
    public ResourceList getOfferedResources() {
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
    public ResourceList getResourceList() {
        return resourceList;
    }
}
