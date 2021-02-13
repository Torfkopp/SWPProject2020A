package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;

/**
 * Message used to state the next player
 *
 * @since 2021-01-15
 */
public class NextPlayerMessage extends AbstractGameMessage {

    /**
     * Constructor
     *
     * @param lobbyName    The lobby name
     * @param activePlayer The active player
     */
    public NextPlayerMessage(String lobbyName, User activePlayer) {
        super(lobbyName, activePlayer);
    }

    /**
     * Gets the player whose turn it is
     *
     * @return User
     */
    public User getActivePlayer() {
        return super.getUser();
    }
}
