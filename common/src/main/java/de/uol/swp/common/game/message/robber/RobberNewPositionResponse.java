package de.uol.swp.common.game.message.robber;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

public class RobberNewPositionResponse extends AbstractResponseMessage {

    private final User player;

    public RobberNewPositionResponse(User player) {
        this.player = player;
    }
}
