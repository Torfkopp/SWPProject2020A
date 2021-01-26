package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;

import java.util.List;
import java.util.Optional;

/**
 * Interface to unify different kinds of UserStores
 * in order to able to exchange them easily.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.user.User
 * @since 2019-08-13
 */
public interface UserStore {

    /**
     * Find a user by its username and password
     *
     * @param username Username of the user to find
     * @param password Password of the user to find
     * @return The user without password information if found
     * @since 2019-08-13
     */
    Optional<User> findUser(String username, String password);

    /**
     * Find a user only by name
     *
     * @param username Username of the user to find
     * @return The user without password information if found
     * @since 2019-08-13
     */
    Optional<User> findUser(String username);

    /**
     * Create a new user
     *
     * @param username Username of the new user
     * @param password Password the user wants to use
     * @param eMail    E-mail address of the new user
     * @return The user without password information
     * @since 2019-08-13
     */
    User createUser(String username, String password, String eMail);

    /**
     * Update a user. Updates only given fields. Username cannot be changed
     *
     * @param username Username of the user to be modified
     * @param password New password
     * @param eMail    New email address
     * @return The user without password information
     * @since 2019-08-13
     */
    User updateUser(String username, String password, String eMail);

    /**
     * Remove a user from the store
     *
     * @param username The username of the user to remove
     * @since 2019-10-10
     */
    void removeUser(String username);

    /**
     * Retrieves the list of all users.
     *
     * @return A list of all users without password information
     * @since 2019-08-13
     */
    List<User> getAllUsers();
}
