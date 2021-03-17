package de.uol.swp.common.game.request;

import de.uol.swp.common.user.User;

public class CheckVictoryPointsRequest extends AbstractGameRequest {

    private final User user;

    public CheckVictoryPointsRequest(String originLobby, User user) {
        super(originLobby);
        this.user = user;
    }

    public User getUser(){return user;}
}
