package de.uol.swp.server.usermanagement;

import de.uol.swp.common.user.User;

import java.util.List;
import java.util.Optional;

/**
 * An interface for all methods of the server user service
 *
 * @author Marco Grawunder
 * @since 2017-03-17
 */
public interface ServerUserService {

    /**
     * Login with username and password
     *
     * @param username the name of the user
     * @param password the password of the user
     * @return a new user object
     * @since 2017-03-17
     */
    User login(String username, String password);

    /**
     * Test, if given user is logged in
     *
     * @param user the user to check for
     * @return true if the User is logged in
     * @since 2019-09-04
     */
    boolean isLoggedIn(User user);

    /**
     * Log out from server
     *
     * @implNote the User Object has to contain a unique identifier in order to
     * remove the correct user
     * @since 2017-03-17
     */
    void logout(User user);

    /**
     * Create a new persistent user
     *
     * @param user The user to create
     * @return the new created user
     * @implNote the User Object has to contain a unique identifier in order to
     * remove the correct user
     * @since 2019-09-02
     */
    User createUser(User user);

    /**
     * Removes a user from the sore
     * <p>
     * Remove the User specified by the User object.
     *
     * @param user The user to remove
     * @implNote the User Object has to contain a unique identifier in order to
     * remove the correct user
     * @since 2019-10-10
     */
    void dropUser(User user);

    /**
     * Update a user
     * <p>
     * Updates the User specified by the User object.
     *
     * @param user the user object containing all infos to
     *             update, if some values are not set, (e.g. password is "")
     *             these fields are not updated
     * @return the updated user object
     * @implNote the User Object has to contain a unique identifier in order to
     * update the correct user
     * @since 2019-09-02
     */
    User updateUser(User user);

    /**
     * Retrieve the list of all current logged in users
     *
     * @return a list of users
     * @since 2017-03-17
     */
    List<User> retrieveAllUsers();

    Optional<User> getUser(String userName);
}
