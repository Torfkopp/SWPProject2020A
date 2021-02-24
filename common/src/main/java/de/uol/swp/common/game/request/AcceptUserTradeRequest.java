package de.uol.swp.common.game.request;

import java.util.Map;

/**
 * Request sent to the server when the responding user wants to accept the trade.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.common.game.request.AbstractGameRequest
 * @since 2021-02-24
 */
public class AcceptUserTradeRequest extends AbstractGameRequest {

    private final String respondingUser;
    private final String offeringUser;
    private final Map<String, Integer> respondingResourceMap;
    private final Map<String, Integer> offeringResourceMap;

    /**
     * Constructor.
     *
     * @param respondingUser        the responding user
     * @param offeringUser          the offering user
     * @param lobbyName             the lobby name
     * @param respondingResourceMap the responding resource map
     * @param offeringResourceMap   the offering resource map
     */
    public AcceptUserTradeRequest(String respondingUser, String offeringUser, String lobbyName,
                                  Map<String, Integer> respondingResourceMap,
                                  Map<String, Integer> offeringResourceMap) {
        super(lobbyName);
        this.respondingUser = respondingUser;
        this.offeringUser = offeringUser;
        this.respondingResourceMap = respondingResourceMap;
        this.offeringResourceMap = offeringResourceMap;
    }

    /**
     * Gets offering user´s resource map.
     *
     * @return Map the offering resource map
     */
    public Map<String, Integer> getOfferingResourceMap() {
        return offeringResourceMap;
    }

    /**
     * Gets name of the offering user.
     *
     * @return String Name of the offering user
     */
    public String getOfferingUser() {
        return offeringUser;
    }

    /**
     * Gets responding user´s resource map.
     *
     * @return Map the responding resource map
     */
    public Map<String, Integer> getRespondingResourceMap() {
        return respondingResourceMap;
    }

    /**
     * Gets the name of the responding user.
     *
     * @return the name of the responding user
     */
    public String getRespondingUser() {
        return respondingUser;
    }
}
