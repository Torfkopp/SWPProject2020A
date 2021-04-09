package de.uol.swp.common.game.robber;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

/**
 * Message sent to a client to ask where
 * the player wants to put the robber.
 *
 * @author Mario Fokken
 * @author Timo Gerken
 * @see de.uol.swp.common.game.robber.RobberNewPositionChosenRequest
 * @since 2021-04-05
 */
public class RobberNewPositionResponse extends AbstractResponseMessage {

    private final User player;

    public RobberNewPositionResponse(User player) {
        this.player = player;
    }
}
