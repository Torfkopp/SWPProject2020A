package de.uol.swp.common.game.message.robber;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;

import java.util.Set;

public class RobberChooseVictimResponse extends AbstractResponseMessage {

    private final User player;
    private final Set<UserOrDummy> victims;

    public RobberChooseVictimResponse(User player, Set<UserOrDummy> victims) {
        this.player = player;
        this.victims = victims;
    }

    public User getPlayer() {
        return player;
    }

    public Set<UserOrDummy> getVictims() {
        return victims;
    }
}
