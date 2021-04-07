package de.uol.swp.common.game.message.robber;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Request sent as a client's answer to the
 * RobberChooseVictimResponse.
 * It is used to know which opponent the user
 * wants to rob a resource card from.
 *
 * @author Mario Fokken
 * @author Timo Gerken
 * @see de.uol.swp.common.game.message.robber.RobberChooseVictimResponse
 * @since 2021-04-06
 */
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
