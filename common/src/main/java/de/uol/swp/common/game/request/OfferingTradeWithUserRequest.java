package de.uol.swp.common.game.request;

import de.uol.swp.common.user.User;

import java.util.Map;

public class OfferingTradeWithUserRequest extends AbstractGameRequest {

    private final User offeringUser;
    private final String respondingUser;
    private final Map<String, Integer> offeringResourceMap;
    private final Map<String, Integer> respondingResourceMap;

    public OfferingTradeWithUserRequest(User offeringUser, String respondingUser, String lobbyName,
                                        Map<String, Integer> offeringResourceMap,
                                        Map<String, Integer> respondingResourceMap) {
        super(lobbyName);
        this.offeringUser = offeringUser;
        this.respondingUser = respondingUser;
        this.offeringResourceMap = offeringResourceMap;
        this.respondingResourceMap = respondingResourceMap;
    }

    public Map<String, Integer> getOfferingResourceMap() {
        return offeringResourceMap;
    }

    public User getOfferingUser() {
        return offeringUser;
    }

    public Map<String, Integer> getRespondingResourceMap() {
        return respondingResourceMap;
    }

    public String getRespondingUser() {
        return respondingUser;
    }
}
