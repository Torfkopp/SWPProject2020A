package de.uol.swp.common.lobby;

import de.uol.swp.common.game.map.configuration.IConfiguration;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;

import java.io.Serializable;
import java.util.Set;

/**
 * Interface to unify lobby objects
 * <p>
 * This is an Interface to allow for multiple types of lobby objects since it is
 * possible that not every client has to have every information of the lobby.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.dto.LobbyDTO
 * @since 2019-10-08
 */
public interface Lobby extends Serializable {

    /**
     * Gets whether commands are allowed or not.
     *
     * @return If comamnds are allowed or not
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    boolean commandsAllowed();

    /**
     * Gets the maximum amount of players for a lobby.
     *
     * @return The maximum amount of players
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    int getMaxPlayers();

    /**
     * Sets the maximum amount of players for a lobby.
     *
     * @param maxPlayers The maximum amount of players
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    void setMaxPlayers(int maxPlayers);

    /**
     * Gets the maximum time for a move.
     *
     * @return Maximum time for a move
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    int getMoveTime();

    /**
     * Sets the maximum time for a move.
     *
     * @param moveTime The maximum time for a move
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    void setMoveTime(int moveTime);

    /**
     * Gets the lobby's name
     *
     * @return A String containing the name of the lobby
     *
     * @since 2019-10-08
     */
    String getName();

    /**
     * Gets the current owner of the lobby
     *
     * @return A User object containing the owner of the lobby
     *
     * @since 2019-10-08
     */
    UserOrDummy getOwner();

    /**
     * Gets a set of all users marked as ready.
     *
     * @return A Set containing all ready users
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @since 2021-01-19
     */
    Set<UserOrDummy> getReadyUsers();

    /**
     * Gets all real users in the lobby
     *
     * @return A Set containing all user in this lobby
     *
     * @author Alwin Bossert
     * @author Temmo Junkhoff
     * @since 2021-03-13
     */
    Set<User> getRealUsers();

    /**
     * Gets all users and dummies in the lobby
     *
     * @return A Set containing all users and dummies in this lobby
     *
     * @author Alwin Bossert
     * @author Temmo Junkhoff
     * @since 2021-03-13
     */
    Set<UserOrDummy> getUserOrDummies();

    /**
     * Gets whether the Lobby is currently in a game or not
     *
     * @author Eric Vuong
     * @author Steven Luong
     * @author Phillip-André Suhr
     * @since 2021-03-01
     */
    boolean isInGame();

    /**
     * Sets whether the Lobby is currently in a game according to the boolean provided
     *
     * @param inGame Whether the Lobby is in a game or not
     *
     * @author Eric Vuong
     * @author Steven Luong
     * @author Phillip-André Suhr
     * @since 2021-03-01
     */
    void setInGame(boolean inGame);

    /**
     * Adds a new user to the lobby
     *
     * @param user The new user to add to the lobby
     *
     * @since 2019-10-08
     */
    void joinUser(UserOrDummy user);

    /**
     * Removes a user from the lobby
     *
     * @param user The user to remove from the lobby
     *
     * @since 2019-10-08
     */
    void leaveUser(UserOrDummy user);

    /**
     * Gets whether the random playfield is enabled or not.
     *
     * @return If random playfield is enabled or not
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    boolean randomPlayfieldEnabled();

    /**
     * Sets whether commands are allowed or not.
     *
     * @param commandsAllowed Whether commands should be enabled or not
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    void setCommandsAllowed(boolean commandsAllowed);

    /**
     * Sets the random playfield attribute.
     *
     * @param randomPlayfieldEnabled Whether the randomPlayfield should be enabled or not
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    void setRandomPlayfieldEnabled(boolean randomPlayfieldEnabled);

    /**
     * Sets the start up phase attribute.
     *
     * @param startUpPhaseEnabled Whether the startUpPhase should be enabled or not
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    void setStartUpPhaseEnabled(boolean startUpPhaseEnabled);

    /**
     * Sets a user as ready
     *
     * @param user The user to mark as ready
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @since 2021-01-19
     */
    void setUserReady(UserOrDummy user);

    /**
     * Gets whether the startUpPhase is enabled or not.
     *
     * @return If the startUpPhase is enabled or not
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    boolean startUpPhaseEnabled();

    /**
     * Marks a user as not ready.
     *
     * @param user The user to mark as not ready
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @since 2021-01-19
     */
    void unsetUserReady(UserOrDummy user);

    /**
     * Changes the owner of the lobby
     *
     * @param user The user who should be the new owner
     *
     * @since 2019-10-08
     */
    void updateOwner(User user);
    
    void setConfiguration(IConfiguration configuration);
    
    IConfiguration getConfiguration();
}
