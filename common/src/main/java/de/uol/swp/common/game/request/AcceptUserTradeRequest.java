package de.uol.swp.common.game.request;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.resourceThingies.resource.resourceListMap.IImmutableResourceListMap;
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
    private final IImmutableResourceListMap demandedResources;
    private final IImmutableResourceListMap offeredResources;

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
                                  IImmutableResourceListMap demandedResources,
                                  IImmutableResourceListMap offeredResources) {
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
    public IImmutableResourceListMap getDemandedResources() {
        return demandedResources;
    }

    /**
     * Gets offering user´s resource map.
     *
     * @return Map of the offered resources
     */
    public IImmutableResourceListMap getOfferedResources() {
        return offeredResources;
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
