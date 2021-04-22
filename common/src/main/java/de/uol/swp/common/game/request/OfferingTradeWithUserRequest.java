package de.uol.swp.common.game.request;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.ResourceListMap;
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
    private final ResourceListMap offeringResourceMap;
    private final ResourceListMap respondingResourceMap;

    /**
     * Constructor
     *
     * @param offeringUser          The offering User
     * @param respondingUser        The responding User
     * @param lobbyName             The name of the lobby
     * @param offeringResourceMap   The offered resources
     * @param respondingResourceMap The responded resources
     */
    public OfferingTradeWithUserRequest(UserOrDummy offeringUser, UserOrDummy respondingUser, LobbyName lobbyName,
                                        ResourceListMap offeringResourceMap,
                                        ResourceListMap respondingResourceMap) {
        super(lobbyName);
        this.offeringUser = offeringUser;
        this.respondingUser = respondingUser;
        this.offeringResourceMap = offeringResourceMap;
        this.respondingResourceMap = respondingResourceMap;
    }

    /**
     * Gets the offering Resources
     *
     * @return Gets the offering Resources as a Map
     */
    public ResourceListMap getOfferingResourceMap() {
        return offeringResourceMap;
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
     * Gets the demanded Resources
     *
     * @return Gets the demanded Resources as a Map
     */
    public ResourceListMap getRespondingResourceMap() {
        return respondingResourceMap;
    }

    /**
     * Gets the responding User
     *
     * @return User-Object of the responding User
     */
    public UserOrDummy getRespondingUser() {
        return respondingUser;
    }
}
