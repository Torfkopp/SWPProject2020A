package de.uol.swp.common.game.robber;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.specialisedUtil.ActorSet;
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

    private final LobbyName lobbyName;
    private final User player;
    private final ActorSet victims;

    /**
     * Constructor
     *
     * @param player  The player choosing the victims
     * @param victims The victims to choose from
     */
    public RobberChooseVictimResponse(LobbyName lobbyName, User player, ActorSet victims) {
        this.lobbyName = lobbyName;
        this.player = player;
        this.victims = victims;
    }

    /**
     * Gets the Lobby's name in which the User has to pick a victim
     *
     * @return The Name of the Lobby in which to choose victims
     *
     * @author Phillip-André Suhr
     * @since 2021-07-01
     */
    public LobbyName getLobbyName() {
        return lobbyName;
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
    public ActorSet getVictims() {
        return victims;
    }
}
