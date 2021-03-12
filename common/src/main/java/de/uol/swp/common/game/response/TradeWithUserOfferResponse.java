package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;

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

    private final User offeringUser;
    private final UserOrDummy respondingUser;
    private final Map<String, Integer> resourceMap;
    private final Map<String, Integer> offeringResourceMap;
    private final Map<String, Integer> respondingResourceMap;

    /**
     * Constructor
     *
     * @param offeringUser          The offering User
     * @param respondingUser        The responding User
     * @param resourceMap           The inventory of the responding user
     * @param offeringResourceMap   The offered resources
     * @param respondingResourceMap The demanded resources
     * @param lobbyName             The name of the lobby
     */
    public TradeWithUserOfferResponse(User offeringUser, UserOrDummy respondingUser, Map<String, Integer> resourceMap,
                                      Map<String, Integer> offeringResourceMap,
                                      Map<String, Integer> respondingResourceMap, String lobbyName) {
        super(lobbyName);
        this.resourceMap = resourceMap;
        this.offeringResourceMap = offeringResourceMap;
        this.respondingResourceMap = respondingResourceMap;
        this.offeringUser = offeringUser;
        this.respondingUser = respondingUser;
    }

    /**
     * Gets the offered resources
     *
     * @return Map of offered resources
     */
    public Map<String, Integer> getOfferingResourceMap() {
        return offeringResourceMap;
    }

    /**
     * Gets the offering User
     *
     * @return A User-Object of the offering User
     */
    public User getOfferingUser() {
        return offeringUser;
    }

    /**
     * Gets the inventory of the responding User
     *
     * @return Map of the inventory of the responding User
     */
    public Map<String, Integer> getResourceMap() {
        return resourceMap;
    }

    /**
     * Gets the demanded resources
     *
     * @return Map of demanded resources
     */
    public Map<String, Integer> getRespondingResourceMap() {
        return respondingResourceMap;
    }

    /**
     * Gets the responding User
     *
     * @return A User-Object of the responding User
     */
    public UserOrDummy getRespondingUser() {
        return respondingUser;
    }
}
