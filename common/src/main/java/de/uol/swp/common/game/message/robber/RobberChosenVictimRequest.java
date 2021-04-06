package de.uol.swp.common.game.message.robber;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;

public class RobberChosenVictimRequest extends AbstractRequestMessage {

    private final String lobby;
    private final User player;
    private final UserOrDummy victim;

    public RobberChosenVictimRequest(String lobby, User player, UserOrDummy victim) {
        this.lobby = lobby;
        this.player = player;
        this.victim = victim;
    }

    public String getLobby() {
        return lobby;
    }

    public User getPlayer() {
        return player;
    }

    public UserOrDummy getVictim() {
        return victim;
    }
}
