package de.uol.swp.common.game.request;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.resourceThingies.resource.resourceListMap.MutableResourceListMap;
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
    private final MutableResourceListMap respondingResourceMap;
    private final MutableResourceListMap offeringResourceMap;

    /**
     * Constructor
     *
     * @param respondingUser        The responding user
     * @param offeringUser          The offering user
     * @param lobbyName             The lobby name
     * @param respondingResourceMap The demanded resources
     * @param offeringResourceMap   The offered resources
     */
    public AcceptUserTradeRequest(UserOrDummy respondingUser, UserOrDummy offeringUser, LobbyName lobbyName,
                                  MutableResourceListMap respondingResourceMap,
                                  MutableResourceListMap offeringResourceMap) {
        super(lobbyName);
        this.respondingUser = respondingUser;
        this.offeringUser = offeringUser;
        this.respondingResourceMap = respondingResourceMap;
        this.offeringResourceMap = offeringResourceMap;
    }

    /**
     * Gets offering user´s resource map.
     *
     * @return Map of the offered resources
     */
    public MutableResourceListMap getOfferingResourceMap() {
        return offeringResourceMap;
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
     * Gets responding user´s resource map.
     *
     * @return Map of the demanded resources
     */
    public MutableResourceListMap getRespondingResourceMap() {
        return respondingResourceMap;
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
