package de.uol.swp.common.game.robber;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.specialisedUtil.UserOrDummyList;
import de.uol.swp.common.user.User;

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
    private final UserOrDummyList victims;

    /**
     * Constructor
     *
     * @param player  The player choosing the victims
     * @param victims The victims to choose from
     */
    public RobberChooseVictimResponse(User player, UserOrDummyList victims) {
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
    public UserOrDummyList getVictims() {
        return victims;
    }
}
