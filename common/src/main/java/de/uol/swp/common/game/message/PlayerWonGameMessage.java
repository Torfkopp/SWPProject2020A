package de.uol.swp.common.game.message;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;

import java.util.Map;

/**
 * Message sent to declare a winner and end the game.
 *
 * @author Steven Luong
 * @author Finn Haase
 * @since 2021-03-22
 */
public class PlayerWonGameMessage extends AbstractGameMessage {

    private final Map<Actor, Map<Integer, Integer>> victoryPointMap;

    /**
     * Constructor
     *
     * @param lobbyName       The lobby this game is taking place in
     * @param user            The user that won the game.
     * @param victoryPointMap
     */
    public PlayerWonGameMessage(LobbyName lobbyName, Actor user, Map<Actor, Map<Integer, Integer>> victoryPointMap) {
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
    public Map<Actor, Map<Integer, Integer>> getVictoryPointMap() {
        return victoryPointMap;
    }
}
