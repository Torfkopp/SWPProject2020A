package de.uol.swp.common.game.request;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;

public class PauseTimerRequest extends AbstractGameRequest {

    public UserOrDummy getUser() {
        return user;
    }

    private UserOrDummy user;

    public PauseTimerRequest(String lobbyName, UserOrDummy user) {
        super(lobbyName);
        this.user = user;
    }
}
