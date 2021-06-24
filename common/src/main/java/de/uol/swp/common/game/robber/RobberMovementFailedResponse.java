package de.uol.swp.common.game.robber;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

/**
 * Response sent to the Client informing him that his attempt to move the robber failed
 *
 * @author Sven Ahrens
 * @since 2021-06-24
 */
public class RobberMovementFailedResponse extends AbstractResponseMessage {

    private final User player;

    /**
     * Constructor
     *
     * @param player The player who tried to move the Robber
     */
    public RobberMovementFailedResponse(User player) {
        this.player = player;
    }

    /**
     * Gets the player
     *
     * @return User player
     */
    public User getPlayer() {
        return player;
    }
}
