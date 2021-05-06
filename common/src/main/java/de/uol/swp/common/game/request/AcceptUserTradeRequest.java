package de.uol.swp.common.game.request;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Request sent to the server when the responding user wants to accept the trade offer.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.common.game.request.AbstractGameRequest
 * @since 2021-02-24
 */
public class AcceptUserTradeRequest extends AbstractGameRequest {

    private final UserOrDummy respondingUser;
    private final UserOrDummy offeringUser;
    private final ResourceList demandedResources;
    private final ResourceList offeredResources;

    /**
     * Constructor
     *
     * @param respondingUser    The responding user
     * @param offeringUser      The offering user
     * @param lobbyName         The lobby name
     * @param demandedResources The demanded resources
     * @param offeredResources  The offered resources
     */
    public AcceptUserTradeRequest(UserOrDummy respondingUser, UserOrDummy offeringUser, LobbyName lobbyName,
                                  ResourceList demandedResources, ResourceList offeredResources) {
        super(lobbyName);
        this.respondingUser = respondingUser;
        this.offeringUser = offeringUser;
        this.demandedResources = demandedResources;
        this.offeredResources = offeredResources;
    }

    /**
     * Gets responding user´s resource map.
     *
     * @return Map of the demanded resources
     */
    public ResourceList getDemandedResources() {
        return demandedResources.create();
    }

    /**
     * Gets offering user´s resource map.
     *
     * @return Map of the offered resources
     */
    public ResourceList getOfferedResources() {
        return offeredResources.create();
    }

    /**
     * Gets name of the offering user.
     *
     * @return Name of the offering user
     */
    public UserOrDummy getOfferingUser() {
        return offeringUser;
    }

    /**
     * Gets the name of the responding user.
     *
     * @return Name of the responding user
     */
    public UserOrDummy getRespondingUser() {
        return respondingUser;
    }
}
