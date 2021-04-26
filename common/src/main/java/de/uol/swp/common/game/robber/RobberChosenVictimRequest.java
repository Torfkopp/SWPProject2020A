package de.uol.swp.common.game.robber;

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
 * @see de.uol.swp.common.game.robber.RobberChooseVictimResponse
 * @since 2021-04-06
 */
public class RobberChosenVictimRequest extends AbstractRequestMessage {

    private final String lobby;
    private final User player;
    private final UserOrDummy victim;

    /**
     * Constructor
     *
     * @param lobby  The lobby's name
     * @param player The player who's chosen the victim
     * @param victim The chosen victim
     */
    public RobberChosenVictimRequest(String lobby, User player, UserOrDummy victim) {
        this.lobby = lobby;
        this.player = player;
        this.victim = victim;
    }

    /**
     * Gets the lobby's name
     *
     * @return String lobby
     */
    public String getLobby() {
        return lobby;
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
     * Gets the victim
     *
     * @return UserOrDummy victim
     */
    public UserOrDummy getVictim() {
        return victim;
    }
}
