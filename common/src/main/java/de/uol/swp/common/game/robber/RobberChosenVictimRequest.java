package de.uol.swp.common.game.robber;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.Actor;
import de.uol.swp.common.user.User;

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

    private final LobbyName lobby;
    private final User player;
    private final Actor victim;

    /**
     * Constructor
     *
     * @param lobby  The lobby's name
     * @param player The player who's chosen the victim
     * @param victim The chosen victim
     */
    public RobberChosenVictimRequest(LobbyName lobby, User player, Actor victim) {
        this.lobby = lobby;
        this.player = player;
        this.victim = victim;
    }

    /**
     * Gets the lobby's name
     *
     * @return String lobby
     */
    public LobbyName getLobby() {
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
     * @return Actor victim
     */
    public Actor getVictim() {
        return victim;
    }
}
