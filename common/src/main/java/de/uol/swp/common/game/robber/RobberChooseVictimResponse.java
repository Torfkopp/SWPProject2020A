package de.uol.swp.common.game.robber;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;

import java.util.Set;

/**
 * Request sent to a client to ask
 * which opponent the player wants
 * to rob a resource card from.
 *
 * @author Mario Fokken
 * @author Timo Gerken
 * @see de.uol.swp.common.game.robber.RobberChosenVictimRequest
 * @since 2021-04-06
 */
public class RobberChooseVictimResponse extends AbstractResponseMessage {

    private final User player;
    private final Set<UserOrDummy> victims;

    /**
     * Constructor
     *
     * @param player  The player choosing the victims
     * @param victims The victims to choose from
     */
    public RobberChooseVictimResponse(User player, Set<UserOrDummy> victims) {
        this.player = player;
        this.victims = victims;
    }

    /**
     * Gets the player
     *
     * @return User player
     */
    public User getPlayer() {
        return player;
    }

    /**
     * Gets the victims
     *
     * @return Set of all victims
     */
    public Set<UserOrDummy> getVictims() {
        return victims;
    }
}