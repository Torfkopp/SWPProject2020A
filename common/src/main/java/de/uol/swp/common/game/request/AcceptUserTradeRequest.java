package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserOrDummy;

import java.util.List;
import java.util.Map;

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
    private final List<Map<String, Object>> demandedResources;
    private final List<Map<String, Object>> offeredResources;

    /**
     * Constructor
     *
     * @param respondingUser    The responding user
     * @param offeringUser      The offering user
     * @param lobbyName         The lobby name
     * @param demandedResources The demanded resources
     * @param offeredResources  The offered resources
     */
    public AcceptUserTradeRequest(UserOrDummy respondingUser, UserOrDummy offeringUser, String lobbyName,
                                  List<Map<String, Object>> demandedResources,
                                  List<Map<String, Object>> offeredResources) {
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
    public List<Map<String, Object>> getDemandedResources() {
        return demandedResources;
    }

    /**
     * Gets offering user´s resource map.
     *
     * @return Map of the offered resources
     */
    public List<Map<String, Object>> getOfferedResources() {
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
