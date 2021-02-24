package de.uol.swp.common.game.request;

import de.uol.swp.common.user.User;

import java.util.Map;

public class OfferingTradeWithUserRequest {

    private final User offeringUser;
    private final String respondingUser;
    private final String lobbyName;
    private final Map<String, Double> offeringResourceMap;
    private final Map<String, Double> respondingResourceMap;

    public OfferingTradeWithUserRequest(User offeringUser, String respondingUser, String lobbyName,
                                        Map<String, Double> offeringResourceMap,
                                        Map<String, Double> respondingResourceMap) {
        this.offeringUser = offeringUser;
        this.respondingUser = respondingUser;
        this.lobbyName = lobbyName;
        this.offeringResourceMap = offeringResourceMap;
        this.respondingResourceMap = respondingResourceMap;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public Map<String, Double> getOfferingResourceMap() {
        return offeringResourceMap;
    }

    public User getOfferingUser() {
        return offeringUser;
    }

    public Map<String, Double> getRespondingResourceMap() {
        return respondingResourceMap;
    }

    public String getRespondingUser() {
        return respondingUser;
    }
}
