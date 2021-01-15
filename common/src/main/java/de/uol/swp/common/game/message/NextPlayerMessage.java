package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;

/**
 * Message used to state the next player
 *
 * @since 2021-01-15
 */
public class NextPlayerMessage extends AbstractGameMessage {
    User activePlayer;

    /**
     * Constructor
     *
     * @param ActivePlayer The active player
     */
    public NextPlayerMessage(User ActivePlayer) {
        this.activePlayer = ActivePlayer;
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
