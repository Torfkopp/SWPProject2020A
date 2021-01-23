package de.uol.swp.server.game;

import de.uol.swp.common.user.User;

/**
 * Interface for the general game management
 *
 * @author Alwin Bossert
 * @author Mario Fokken
 * @author Marvin Drees
 * @since 2021-01-23
 */
public interface IGameManagement {

    /**
     * Gets the players whose turn is next
     *
     * @return User object of the next player
     */
    User nextPlayer();

    /**
     * Gets the players who's currently playing
     *
     * @return User object of the current player
     */
    User getActivePlayer();

    /**
     * Gets the lobby
     *
     * @return String lobbyName
     */
    String getLobby();

}
