package de.uol.swp.common.game.response;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.ResourceListMap;
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
    private final ResourceListMap resourceMap;
    private final ResourceListMap offeringResourceMap;
    private final ResourceListMap respondingResourceMap;

    /**
     * Constructor
     *
     * @param offeringUser          The offering User
     * @param resourceMap           The inventory of the responding user
     * @param offeringResourceMap   The offered resources
     * @param respondingResourceMap The demanded resources
     * @param lobbyName             The name of the lobby
     */
    public TradeWithUserOfferResponse(UserOrDummy offeringUser,
                                      ResourceListMap resourceMap, ResourceListMap offeringResourceMap,
                                      ResourceListMap respondingResourceMap, LobbyName lobbyName) {
        super(lobbyName);
        this.resourceMap = resourceMap;
        this.offeringResourceMap = offeringResourceMap;
        this.respondingResourceMap = respondingResourceMap;
        this.offeringUser = offeringUser;
    }

    /**
     * Gets the offered resources
     *
     * @return Map of offered resources
     */
    public ResourceListMap getOfferingResourceMap() {
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
    public ResourceListMap getResourceMap() {
        return resourceMap;
    }

    /**
     * Gets the demanded resources
     *
     * @return Map of demanded resources
     */
    public ResourceListMap getRespondingResourceMap() {
        return respondingResourceMap;
    }
}
