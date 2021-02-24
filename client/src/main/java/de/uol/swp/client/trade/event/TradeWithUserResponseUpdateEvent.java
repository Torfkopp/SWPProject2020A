package de.uol.swp.client.trade.event;

import de.uol.swp.common.user.User;

public class TradeWithUserResponseUpdateEvent {
    private final String responseUser;
    private final String lobbyName;
    private final User offeringUser;

    public TradeWithUserResponseUpdateEvent(String responseUser, String lobbyName, User offeringUser) {
        this.responseUser = responseUser;
        this.lobbyName = lobbyName;
        this.offeringUser = offeringUser;
    }

    public String getResponseUser() {
        return responseUser;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public User getOfferingUser() {
        return offeringUser;
    }
}
