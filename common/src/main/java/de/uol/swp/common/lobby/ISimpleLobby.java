package de.uol.swp.common.lobby;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;

import java.util.Set;

public interface ISimpleLobby {


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
    Set<UserOrDummy> getUsers();

    /**
     * Gets if commands are allowed.
     *
     * @return The boolean
     *
     * @author Temmo Junkhoff
     * @since 2021-05-03
     */
    boolean isCommandsAllowed();

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
    boolean isRandomPlayfieldEnabled();

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
