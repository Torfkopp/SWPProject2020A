package de.uol.swp.client.trade;

import de.uol.swp.common.user.User;

public class ShowTradeWithUserViewEvent {
    private final User user;

    public ShowTradeWithUserViewEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
