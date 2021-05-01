package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserOrDummy;

public class UnpauseTimerRequest extends AbstractGameRequest {

    private UserOrDummy user;

    public UnpauseTimerRequest(String lobbyName, UserOrDummy user) {
        super(lobbyName);
        this.user = user;
    }

    public UserOrDummy getUser() {
        return user;
    }
}

