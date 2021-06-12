package de.uol.swp.common.game.message;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

import java.util.Map;

/**
 * Message sent to declare a winner and end the game.
 *
 * @author Steven Luong
 * @author Finn Haase
 * @since 2021-03-22
 */
public class PlayerWonGameMessage extends AbstractGameMessage {

    private final Map<UserOrDummy, Map<Integer, Integer>> victoryPointMap;

    /**
     * Constructor
     *
     * @param lobbyName       The lobby this game is taking place in
     * @param user            The user that won the game.
     * @param victoryPointMap
     */
    public PlayerWonGameMessage(LobbyName lobbyName, UserOrDummy user,
                                Map<UserOrDummy, Map<Integer, Integer>> victoryPointMap) {
        super(lobbyName, user);
        this.victoryPointMap = victoryPointMap;
    }

    public Map<UserOrDummy, Map<Integer, Integer>> getVictoryPointMap() {
        return victoryPointMap;
    }
}
