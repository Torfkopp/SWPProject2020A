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
public interface IUserStore {

    /**
     * Create a new user
     * <p>
     * This method registers the user with its specific and unique username,
     * password and e-mail and saves it in the database.
     *
     * @param username Username of the new user
     * @param password Password the user wants to use
     * @param eMail    E-mail address of the new user
     *
     * @return The user without password information
     */
    User createUser(String username, String password, String eMail) throws RuntimeException;

    /**
     * Find a user only by ID
     * <p>
     * This method finds and returns the user specified by the provided ID
     * without a password comparison
     *
     * @param id ID of the user to find
     *
     * @return The user without password information if found
     *
     * @author Aldin Dervisi
     * @author Phillip-André Suhr
     * @since 2021-02-23
     */
    Optional<User> findUser(int id);

    /**
     * Find a user only by name
     * <p>
     * This method finds and returns the specific user
     * from the database without a password comparison.
     *
     * @param username Username of the user to find
     *
     * @return The user without password information if found
     */
    Optional<User> findUser(String username);

    /**
     * Find a user by its username and password
     * <p>
     * This method searches for a user that matches both
     * the provided username and password and returns a
     * UserDTO for the matching result.
     *
     * @param username Username of the user to find
     * @param password Password of the user to find
     *
     * @return The user without password information if found
     */
    Optional<User> findUser(String username, String password);

    /**
     * Retrieves the list of all users.
     * <p>
     * This method dumps the whole database and puts
     * the data from each row into a UserDTO which then
     * gets put into a list.
     *
     * @return A list of all users without password information
     */
    List<User> getAllUsers();

    /**
     * Gets the ID that will be assigned to the NEXT newly created user
     *
     * @return The next user ID, -1 if an error occurred when looking it up
     *
     * @author Aldin Dervisi
     * @author Phillip-André Suhr
     * @since 2021-02-26
     */
    int getNextUserID();

    /**
     * Remove a user form the store with their ID
     * <p>
     * This method removes the row matching the provided ID.
     *
     * @param id The ID of the user to remove
     *
     * @author Aldin Dervisi
     * @author Phillip-André Suhr
     * @since 2021-02-23
     */
    void removeUser(int id);

    /**
     * Remove a user from the store with their username
     * <p>
     * This method removes the row matching the provided username.
     *
     * @param username The username of the user to remove
     *
     * @since 2019-10-10
     */
    void removeUser(String username);

    /**
     * Update a user. Updates only given fields.
     * ID cannot be changed
     * <p>
     * This method allows the user to change his unique username, password or e-mail.
     * The user will not be able to update his username or e-mail into already registered ones.
     *
     * @param id       ID of the user to be modified
     * @param username New username
     * @param password New password
     * @param eMail    New email address
     *
     * @return The user without password information
     *
     * @author Aldin Dervisi
     * @author Phillip-André Suhr
     * @since 2021-02-23
     */
    User updateUser(int id, String username, String password, String eMail) throws RuntimeException;

    /**
     * Update a user. Updates only given fields.
     * Username cannot be changed with this method
     * <p>
     * This method allows the user to change his password or e-mail.
     * The user will not be able to update his e-mail into already registered ones.
     *
     * @param username Username of the user to be modified
     * @param password New password
     * @param eMail    New email address
     *
     * @return The user without password information
     */
    User updateUser(String username, String password, String eMail) throws RuntimeException;
}
