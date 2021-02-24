package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;

import java.util.Map;

public class TradeWithUserOfferMessage extends AbstractGameMessage{
    private final String respondingUserName;
    private final Map<String,Integer> resourceMap;

    public TradeWithUserOfferMessage(User user, String respondingUserName, String lobbyName,
                                     Map<String, Integer> resourceMap) {
        super(lobbyName, user);
        this.respondingUserName = respondingUserName;
        this.resourceMap = resourceMap;
    }

    public String getRespondingUserName() {
        return respondingUserName;
    }

    public Map<String, Integer> getResourceMap() {
        return resourceMap;
    }
}
