package de.uol.swp.server.usermanagement;

import de.uol.swp.common.user.User;

import java.util.List;
import java.util.Optional;

/**
 * An interface for all methods of the server's UserManagement
 *
 * @author Marco Grawunder
 * @since 2017-03-17
 */
public interface IUserManagement {

    /**
     * Create a new permanent user
     *
     * @param user The user to create
     *
     * @return The newly created user
     *
     * @implNote The User object has to contain a unique identifier in order to
     * remove the correct user
     * @since 2019-09-02
     */
    User createUser(User user);

    /**
     * Removes a user from the store
     * <p>
     * Removes the user specified by the User object.
     *
     * @param user The user to remove
     *
     * @implNote The User object has to contain a unique identifier in order to
     * remove the correct user
     * @since 2019-10-10
     */
    void dropUser(User user);

    /**
     * Gets a user from the store with only their ID
     * <p>
     * Gets the user specified by the ID.
     *
     * @param id The ID of the user to get
     *
     * @return Optional containing the User object if found, empty otherwise
     *
     * @author Aldin Dervisi
     * @author Phillip-André Suhr
     * @since 2021-02-23
     */
    Optional<User> getUser(int id);

    /**
     * Gets a user from the store with only their name
     * <p>
     * Gets the user specified by the userName.
     *
     * @param userName The name of the user to get
     *
     * @return Optional containing the User object if found, empty otherwise
     *
     * @implNote The User object has to contain a unique identifier in order to
     * get the correct user
     */
    Optional<User> getUser(String userName);

    /**
     * Gets a user from the store using their name and password.
     * <p>
     * Gets the user specified by the userName and password.
     *
     * @param userName The name of the user to get
     * @param password The password of the user to get
     *
     * @return Optional containing the User object if found, empty otherwise
     *
     * @implNote The User object has to contain a unique identifier in order to
     * get the correct user
     */
    Optional<User> getUserWithPassword(String userName, String password);

    /**
     * Test if given user is logged in
     *
     * @param user The user to check for
     *
     * @return True if the User is logged in
     *
     * @since 2019-09-04
     */
    boolean isLoggedIn(User user);

    /**
     * Login with username and password
     *
     * @param username The name of the user
     * @param password The password of the user
     *
     * @return A new user object
     */
    User login(String username, String password);

    /**
     * Log out from server
     *
     * @implNote The User object has to contain a unique identifier in order to
     * remove the correct user
     */
    void logout(User user);

    /**
     * Retrieve the list of all currently logged in users
     *
     * @return A list of users
     */
    List<User> retrieveAllUsers();

    /**
     * Update a user
     * <p>
     * Updates the user specified by the User object.
     *
     * @param user The user object containing all infos to
     *             update, if some values are not set, (e.g. password is "")
     *             these fields are not updated
     *
     * @return The updated user object
     *
     * @throws de.uol.swp.server.usermanagement.UserManagementException Thrown if
     *                                                                  the provided
     *                                                                  User is unknown
     * @implNote The User object has to contain a unique identifier in order to
     * update the correct user
     * @since 2019-09-02
     */
    User updateUser(User user) throws UserManagementException;
}
