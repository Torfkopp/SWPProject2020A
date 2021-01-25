package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;

/**
 * Message used to state the next player
 *
 * @since 2021-01-15
 */
public class NextPlayerMessage extends AbstractGameMessage {
    String lobby;
    User activePlayer;

    /**
     * Constructor
     *
     * @param ActivePlayer The active player
     */
    public NextPlayerMessage(String lobby, User ActivePlayer) {
        this.lobby = lobby;
        this.activePlayer = ActivePlayer;
    }

    /**
     * Gets the lobby
     *
     * @return String The lobby's name
     */
    public String getLobby() {
        return lobby;
    }

    /**
     * Gets the player whose turn it is
     *
     * @return User
     */
    public User getActivePlayer() {
        return activePlayer;
    }
}
