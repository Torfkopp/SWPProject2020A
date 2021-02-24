package de.uol.swp.client.trade.event;

import de.uol.swp.common.user.User;

public class ShowTradeWithUserAcceptViewEvent {
    private final User user;
    private final String tradingUser;
    private final String lobbyName;

    public ShowTradeWithUserAcceptViewEvent(User user, String tradingUser, String lobbyName) {
        this.user = user;
        this.tradingUser = tradingUser;
        this.lobbyName = lobbyName;
    }

    public User getUser() {
        return user;
    }

    public String getTradingUser() {
        return tradingUser;
    }

    public String getLobbyName() {
        return lobbyName;
    }
}
