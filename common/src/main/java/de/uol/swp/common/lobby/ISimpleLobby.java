package de.uol.swp.common.lobby;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;

import java.io.Serializable;
import java.util.Set;

/**
 * The interface for a simple lobby used to send lobby data over the network.
 *
 * @author Temmo Junkhoff
 */
public interface ISimpleLobby extends Serializable {

    /**
     * Gets the max players.
     *
     * @return The max players
     *
     * @author Temmo Junkhoff
     * @since 2021-05-03
     */
    int getMaxPlayers();

    /**
     * Gets the move time.
     *
     * @return The move time
     *
     * @author Temmo Junkhoff
     * @since 2021-05-03
     */
    int getMoveTime();

    /**
     * Gets the lobby name.
     *
     * @return The lobby name
     *
     * @author Temmo Junkhoff
     * @since 2021-05-03
     */
    LobbyName getName();

    /**
     * Gets the owner.
     *
     * @return The owner
     *
     * @author Temmo Junkhoff
     * @since 2021-05-03
     */
    User getOwner();

    /**
     * Gets the ready users.
     *
     * @return The ready users
     *
     * @author Temmo Junkhoff
     * @since 2021-05-03
     */
    Set<UserOrDummy> getReadyUsers();

    /**
     * Gets the users.
     *
     * @return The users
     *
     * @author Temmo Junkhoff
     * @since 2021-05-03
     */
    Set<UserOrDummy> getUserOrDummies();

    int getMaxTradeDiff();

    /**
     * Returns if the lobby has a password
     *
     * @return The boolean indicating if the lobby has a password
     *
     * @author Temmo Junkhoff
     * @since 2021-05-04
     */
    boolean hasPassword();

    /**
     * Gets if the lobby is inGame.
     *
     * @return The boolean
     *
     * @author Temmo Junkhoff
     * @since 2021-05-03
     */
    boolean isInGame();

    /**
     * Gets if the random playfield is enabled.
     *
     * @return The boolean
     *
     * @author Temmo Junkhoff
     * @since 2021-05-03
     */
    boolean isRandomPlayFieldEnabled();

    /**
     * Gets if the start up phase is enabled boolean.
     *
     * @return The boolean
     *
     * @author Temmo Junkhoff
     * @since 2021-05-03
     */
    boolean isStartUpPhaseEnabled();
}
