package de.uol.swp.client.user;

import de.uol.swp.common.user.User;

/**
 * An interface for all methods of the client user service
 * <p>
 * As the communication with the server is based on events, the
 * returns of the call must be handled by events
 *
 * @author Marco Grawunder
 * @since 2017-03-17
 */
public interface ClientUserService {

    /**
     * Login with username and password
     *
     * @param username the name of the user
     * @param password the password of the user
     * @since 2017-03-17
     */
    void login(String username, String password);

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
     * @implNote the User Object has to contain a unique identifier in order to
     * remove the correct user
     * @since 2019-09-02
     */
    void createUser(User user);

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
     * @implNote the User Object has to contain a unique identifier in order to
     * update the correct user
     * @since 2019-09-02
     */
    void updateUser(User user);

    /**
     * Retrieve the list of all current logged in users
     *
     * @since 2017-03-17
     */
    void retrieveAllUsers();

    /**
     * Update a user password
     *
     * @param user        The User whose password should be updated
     * @param oldPassword The User's old password
     * @author Eric Vuong
     * @author Steven Luong
     * @since 2020-12-17
     */
    void updateUserPassword(User user, String oldPassword);
}
