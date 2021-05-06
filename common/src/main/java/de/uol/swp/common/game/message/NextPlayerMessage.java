package de.uol.swp.common.game.message;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Message used to state the next player
 *
 * @since 2021-01-15
 */
public class NextPlayerMessage extends AbstractGameMessage {

    private final int currentRound;

    /**
     * Constructor
     *
     * @param lobbyName    The lobby name
     * @param activePlayer The active player
     */
    public NextPlayerMessage(LobbyName lobbyName, UserOrDummy activePlayer, int currentRound) {
        super(lobbyName, activePlayer);
        this.currentRound = currentRound;
    }

    /**
     * Gets the player whose turn it is
     *
     * @return User
     */
    public UserOrDummy getActivePlayer() {
        return super.getUser();
    }

    /**
     * Gets the current Round the game is in
     *
     * @author Aldin Dervisi
     * @since 2021-05-01
     */
    public int getCurrentRound() {
        return currentRound;
    }
}