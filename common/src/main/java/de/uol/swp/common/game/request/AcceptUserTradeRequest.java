package de.uol.swp.common.game.request;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.Resource;
import de.uol.swp.common.user.UserOrDummy;

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
    private final Map<Resource, Integer> respondingResourceMap;
    private final Map<Resource, Integer> offeringResourceMap;

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
                                  Map<Resource, Integer> respondingResourceMap,
                                  Map<Resource, Integer> offeringResourceMap) {
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
    public Map<Resource, Integer> getOfferingResourceMap() {
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
    public Map<Resource, Integer> getRespondingResourceMap() {
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
