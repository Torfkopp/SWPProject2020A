package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.User;

public class CheckVictoryPointsResponse extends AbstractLobbyResponse {
    private final User user;

    public CheckVictoryPointsResponse(String lobbyName, User user) {
        super(lobbyName);
        this.user = user;
    }

    public User getUser(){return user;}
}
