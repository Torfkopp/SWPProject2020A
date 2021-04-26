package de.uol.swp.client.user;

import com.google.common.hash.Hashing;
import de.uol.swp.common.user.User;

import java.nio.charset.StandardCharsets;

/**
 * An interface for all methods of the client's UserService
 * <p>
 * Since the communication with the server is based on events,
 * the return of the calls must be handled by events
 *
 * @author Marco Grawunder
 * @since 2017-03-17
 */
@SuppressWarnings("UnstableApiUsage")
public interface IUserService {

    /**
     * Create a new permanent user
     * <p>
     * This method creates the user specified by the User object permanently.
     *
     * @param user The user to create
     *
     * @since 2019-09-02
     */
    void createUser(User user);

    /**
     * Remove a user from the server
     * <p>
     * This method removes the user specified by the User object.
     *
     * @param user     The user to remove
     * @param password The users password
     *
     * @implNote The User object has to contain a unique identifier in order to
     * remove the correct user
     * @since 2019-10-10
     */
    void dropUser(User user, String password);

    /**
     * Get the currently logged in user
     *
     * @return The user that is currently logged in
     *
     * @author Phillip-André Suhr
     * @author Steven Luong
     * @since 2021-04-04
     */
    User getLoggedInUser();

    /**
     * Set the currently logged in user
     *
     * @param loggedInUser The user that is now logged in
     *
     * @author Phillip-André Suhr
     * @author Steven Luong
     * @since 2021-04-04
     */
    void setLoggedInUser(User loggedInUser);

    /**
     * Calculates the hash for a given String
     *
     * @param toHash The string to calculate the hash for
     *
     * @return String containing the calculated hash
     *
     * @implSpec The hash method used is sha256
     * @author Phillip-André Suhr
     * @since 2021-04-16
     */
    default String hash(String toHash) {
        return Hashing.sha256().hashString(toHash, StandardCharsets.UTF_8).toString();
    }

    /**
     * Login with a username and a password
     * <p>
     * This method logs the user in.
     *
     * @param username     the user's name
     * @param passwordHash the user's hashed password
     *
     * @since 2017-03-17
     */
    void login(String username, String passwordHash);

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
     * Retrieve the list of all currently logged in users
     *
     * @since 2017-03-17
     */
    void retrieveAllUsers();

    /**
     * Update a user's account details
     *
     * @param user              The user changing the Account Details
     * @param oldHashedPassword The hashed password that is to be changed
     * @param oldUsername       The Username that is to be changed
     * @param oldEMail          The EMail that is to be changed
     *
     * @author Eric Vuong
     * @author Steven Luong
     * @since 2020-12-17
     */
    void updateAccountDetails(User user, String oldHashedPassword, String oldUsername, String oldEMail);
}
