package de.uol.swp.common.game.message;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.specialisedUtil.VictoryPointOverTimeMap;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Message sent to declare a winner and end the game.
 *
 * @author Steven Luong
 * @author Finn Haase
 * @since 2021-03-22
 */
public class PlayerWonGameMessage extends AbstractGameMessage {

    private final VictoryPointOverTimeMap victoryPointMap;

    /**
     * Constructor
     *
     * @param lobbyName       The lobby this game is taking place in
     * @param user            The user that won the game.
     * @param victoryPointMap VictoryPointOverTimeMap
     */
    public PlayerWonGameMessage(LobbyName lobbyName, UserOrDummy user,
                                VictoryPointOverTimeMap victoryPointMap) {
        super(lobbyName, user);
        this.victoryPointMap = victoryPointMap;
    }

    /**
     * Gets the current Victory Point Map
     *
     * @return Map of Victory Point Map
     *
     * @author Aldin Dervisi
     * @since 2021-06-12
     */
    public VictoryPointOverTimeMap getVictoryPointMap() {
        return victoryPointMap;
    }
}
