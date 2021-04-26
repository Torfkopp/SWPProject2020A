package de.uol.swp.server.usermanagement;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import de.uol.swp.common.user.User;
import de.uol.swp.server.usermanagement.store.UserStore;

import java.util.*;

/**
 * Handles most user related issues, e.g. login/logout
 *
 * @author Marco Grawunder
 * @see de.uol.swp.server.usermanagement.IUserManagement
 * @since 2019-08-05
 */
public class UserManagement implements IUserManagement {

    private final UserStore userStore;
    private final SortedMap<Integer, User> loggedInUsers = new TreeMap<>();

    /**
     * Constructor
     *
     * @param userStore Object of the UserStore to be used
     *
     * @see de.uol.swp.server.usermanagement.store.UserStore
     */
    @Inject
    public UserManagement(UserStore userStore) {
        this.userStore = userStore;
    }

    @Override
    public User createUser(User userToCreate) throws UserManagementException {
        Optional<User> user = userStore.findUser(userToCreate.getUsername());
        if (user.isPresent()) {
            throw new UserManagementException("Username already used!");
        }
        try {
            return userStore
                    .createUser(userToCreate.getUsername(), userToCreate.getPassword(), userToCreate.getEMail());
        } catch (RuntimeException e) {
            throw new UserManagementException(e.getMessage());
        }
    }

    @Override
    public void dropUser(User userToDrop) throws UserManagementException {
        Optional<User> user = userStore.findUser(userToDrop.getUsername());
        if (user.isEmpty()) {
            throw new UserManagementException("Username unknown!");
        }
        userStore.removeUser(userToDrop.getID());
    }

    @Override
    public Optional<User> getUser(int id) {
        return userStore.findUser(id);
    }

    @Override
    public Optional<User> getUser(String userName) {
        return userStore.findUser(userName);
    }

    @Override
    public Optional<User> getUser(String userName, String password) {
        return userStore.findUser(userName, password);
    }

    @Override
    public boolean isLoggedIn(User username) {
        return loggedInUsers.containsKey(username.getID());
    }

    @Override
    public User login(String username, String password) throws SecurityException {
        Optional<User> user = userStore.findUser(username, password);
        if (user.isPresent()) {
            this.loggedInUsers.put(user.get().getID(), user.get());
            return user.get();
        } else {
            throw new SecurityException("Cannot auth user " + username);
        }
    }

    @Override
    public void logout(User user) {
        loggedInUsers.remove(user.getID());
    }

    @Override
    public List<User> retrieveAllUsers() {
        return userStore.getAllUsers();
    }

    @Override
    public User updateUser(User userToUpdate) throws UserManagementException {
        Optional<User> user = userStore.findUser(userToUpdate.getID());
        if (user.isEmpty()) {
            throw new UserManagementException("User unknown!");
        }
        try {
            // Only update if there are new values
            String newUsername = firstNotNull(userToUpdate.getUsername(), user.get().getUsername());
            String newPassword = firstNotNull(userToUpdate.getPassword(), user.get().getPassword());
            String newEMail = firstNotNull(userToUpdate.getEMail(), user.get().getEMail());
            return userStore.updateUser(user.get().getID(), newUsername, newPassword, newEMail);
        } catch (RuntimeException e) {
            throw new UserManagementException(e.getMessage());
        }
    }

    /**
     * Subfunction of the updateUser method
     * <p>
     * This method is used to set the new user values to the old ones
     * if the values in the update request were empty.
     *
     * @param firstValue  Value to update to. Either an empty String or null
     * @param secondValue The old value
     *
     * @return String containing the value to be used in the update command
     *
     * @since 2019-08-05
     */
    private String firstNotNull(String firstValue, String secondValue) {
        return Strings.isNullOrEmpty(firstValue) ? secondValue : firstValue;
    }
}
