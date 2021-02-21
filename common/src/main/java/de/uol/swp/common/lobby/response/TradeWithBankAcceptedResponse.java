package de.uol.swp.common.lobby.response;

import de.uol.swp.common.user.User;

public class TradeWithBankAcceptedResponse extends AbstractLobbyResponse {

    private final User user;

    public TradeWithBankAcceptedResponse(User user, String lobbyName) {
        super(lobbyName);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
