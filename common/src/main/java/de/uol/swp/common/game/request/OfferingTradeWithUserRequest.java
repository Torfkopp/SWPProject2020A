package de.uol.swp.common.game.request;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

/**
 * This request contains the necessary information for a trade
 * between 2 users.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.common.game.request.AbstractGameRequest
 * @since 2021-02-25
 */
public class OfferingTradeWithUserRequest extends AbstractGameRequest {

    private final UserOrDummy offeringUser;
    private final UserOrDummy respondingUser;
    private final ResourceList offeredResources;
    private final ResourceList demandedResources;
    private final boolean counterOffer;

    /**
     * Constructor
     *
     * @param offeringUser      The offering User
     * @param respondingUser    The responding User
     * @param lobbyName         The name of the lobby
     * @param offeredResources  The offered resources
     * @param demandedResources The responded resources
     * @param counterOffer      Whether the offer is a counter offer or not
     */
    public OfferingTradeWithUserRequest(UserOrDummy offeringUser, UserOrDummy respondingUser, LobbyName lobbyName,
                                        ResourceList offeredResources, ResourceList demandedResources,
                                        boolean counterOffer) {
        super(lobbyName);
        this.offeringUser = offeringUser;
        this.respondingUser = respondingUser;
        this.offeredResources = offeredResources;
        this.demandedResources = demandedResources;
        this.counterOffer = counterOffer;
    }

    /**
     * Gets the demanded Resources
     *
     * @return Gets the demanded Resources as a Map
     */
    public ResourceList getDemandedResources() {
        return demandedResources;
    }

    /**
     * Gets the offering Resources
     *
     * @return Gets the offering Resources as a Map
     */
    public ResourceList getOfferedResources() {
        return offeredResources;
    }

    /**
     * Gets the offering User
     *
     * @return User-Object of the offering User
     */
    public UserOrDummy getOfferingUser() {
        return offeringUser;
    }

    /**
     * Gets the responding User
     *
     * @return User-Object of the responding User
     */
    public UserOrDummy getRespondingUser() {
        return respondingUser;
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
