package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;

import java.util.Map;

public class TradeWithUserOfferMessage extends AbstractGameMessage {

    private final String respondingUserName;
    private final Map<String, Integer> resourceMap;
    private final Map<String, Integer> offeringResourceMap;
    private final Map<String, Integer> respondingResourceMap;

    public TradeWithUserOfferMessage(User user, String respondingUserName, String lobbyName,
                                     Map<String, Integer> resourceMap, Map<String, Integer> offeringResourceMap,
                                     Map<String, Integer> respondingResourceMap) {
        super(lobbyName, user);
        this.respondingUserName = respondingUserName;
        this.resourceMap = resourceMap;
        this.offeringResourceMap = offeringResourceMap;
        this.respondingResourceMap = respondingResourceMap;
    }

    public Map<String, Integer> getOfferingResourceMap() {
        return offeringResourceMap;
    }

    public Map<String, Integer> getResourceMap() {
        return resourceMap;
    }

    public Map<String, Integer> getRespondingResourceMap() {
        return respondingResourceMap;
    }

    public String getRespondingUserName() {
        return respondingUserName;
    }
}
