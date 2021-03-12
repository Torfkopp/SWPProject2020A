package de.uol.swp.common.lobby;

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
    User getOwner();

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
     * Gets all users in the lobby
     *
     * @return A Set containing all user in this lobby
     *
     * @since 2019-10-08
     */
    Set<User> getUsers();

    /**
     * Gets all users and dummies in the lobby
     *
     * @return A Set containing all users and dummies in this lobby
     *
     * @since 2019-10-08
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
}
