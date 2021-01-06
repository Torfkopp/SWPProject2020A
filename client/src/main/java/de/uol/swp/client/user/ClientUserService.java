package de.uol.swp.client.user;

import de.uol.swp.common.user.User;

/**
 * An interface for all methods of the client's UserService
 * <p>
 * Since the communication with the server is based on events,
 * the return of the calls must be handled by events
 *
 * @author Marco Grawunder
 * @since 2017-03-17
 */
public interface ClientUserService {

    /**
     * Login with a username and a password
     * <p>
     * This method logs the user in.
     *
     * @param username the user's name
     * @param password the user's password
     * @since 2017-03-17
     */
    void login(String username, String password);

    /**
     * Log out from server
     * <p>
     * This method logs the user specified by the User object out.
     *
     * @implNote The User object has to contain a unique identifier in order to
     * remove the correct user
     * @since 2017-03-17
     */
    void logout(User user);

    /**
     * Create a new permanent user
     * <p>
     * This method creates the user specified by the User object permanently.
     *
     * @param user The user to create
     * @implNote The User object has to contain a unique identifier in order to
     * remove the correct user
     * @since 2019-09-02
     */
    void createUser(User user);

    /**
     * Remove a user from the server
     * <p>
     * This method removes the user specified by the User object.
     *
     * @param user The user to remove
     * @implNote The User object has to contain a unique identifier in order to
     * remove the correct user
     * @since 2019-10-10
     */
    void dropUser(User user);

    /**
     * Update a user
     * <p>
     * This method updates the user specified by the User object.
     *
     * @param user The User object containing all infos to update.
     *             If some values are not set (e.g. password is ""),
     *             these fields are not updated
     * @implNote The User object has to contain a unique identifier in order to
     * update the correct user
     * @since 2019-09-02
     */
    void updateUser(User user);

    /**
     * Retrieve the list of all currently logged in users
     *
     * @since 2017-03-17
     */
    void retrieveAllUsers();

    /**
     * Update a user's password
     *
     * @param user        The user changing the password
     * @param oldPassword The password that is to be changed
     * @author Eric Vuong
     * @author Steven Luong
     * @since 2020-12-17
     */
    void updateUserPassword(User user, String oldPassword);
}
