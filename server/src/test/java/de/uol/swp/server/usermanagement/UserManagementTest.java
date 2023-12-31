package de.uol.swp.server.usermanagement;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.usermanagement.store.IUserStore;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserManagementTest {

    private static final int NO_USERS = 10;
    private static final List<User> users;
    private static final User userNotInStore = new UserDTO(NO_USERS, "marco" + NO_USERS, "marco" + NO_USERS,
                                                           "marco" + NO_USERS + "@grawunder.de");

    static {
        users = new ArrayList<>();
        for (int i = 0; i < NO_USERS; i++) {
            users.add(new UserDTO(i, "marco" + i, "marco" + i, "marco" + i + "@grawunder.de"));
        }
        Collections.sort(users);
    }

    protected IUserManagement getDefaultManagement() {
        IUserStore store = new MainMemoryBasedUserStore();
        List<User> users = getDefaultUsers();
        users.forEach(u -> store.createUser(u.getUsername(), u.getPassword(), u.getEMail()));
        return new UserManagement(store);
    }

    protected List<User> getDefaultUsers() {
        return Collections.unmodifiableList(users);
    }

    @Test
    void createUser() {
        IUserManagement management = getDefaultManagement();

        management.createUser(userNotInStore);

        // Creation leads not to log in
        assertFalse(management.isLoggedIn(userNotInStore));

        // Only way to test, if user is stored
        management.login(userNotInStore.getUsername(), userNotInStore.getPassword());

        assertTrue(management.isLoggedIn(userNotInStore));
    }

    @Test
    void createUserAlreadyExisting() {
        IUserManagement management = getDefaultManagement();
        User userToCreate = users.get(0);

        assertThrows(UserManagementException.class, () -> management.createUser(userToCreate));
    }

    @Test
    void dropUser() {
        IUserManagement management = getDefaultManagement();
        management.createUser(userNotInStore);

        management.dropUser(userNotInStore);

        assertThrows(SecurityException.class,
                     () -> management.login(userNotInStore.getUsername(), userNotInStore.getPassword()));
    }

    @Test
    void dropUserNotExisting() {
        IUserManagement management = getDefaultManagement();
        assertThrows(UserManagementException.class, () -> management.dropUser(userNotInStore));
    }

    @Test
    void loginUser() {
        IUserManagement management = getDefaultManagement();
        User userToLogIn = users.get(0);

        management.login(userToLogIn.getUsername(), userToLogIn.getPassword());

        assertTrue(management.isLoggedIn(userToLogIn));
    }

    @Test
    void loginUserEmptyPassword() {
        IUserManagement management = getDefaultManagement();
        User userToLogIn = users.get(0);

        assertThrows(SecurityException.class, () -> management.login(userToLogIn.getUsername(), ""));

        assertFalse(management.isLoggedIn(userToLogIn));
    }

    @Test
    void loginUserWrongPassword() {
        IUserManagement management = getDefaultManagement();
        User userToLogIn = users.get(0);
        User secondUser = users.get(1);

        assertThrows(SecurityException.class,
                     () -> management.login(userToLogIn.getUsername(), secondUser.getPassword()));

        assertFalse(management.isLoggedIn(userToLogIn));
    }

    @Test
    void logoutUser() {
        IUserManagement management = getDefaultManagement();
        User userToLogin = users.get(0);

        management.login(userToLogin.getUsername(), userToLogin.getPassword());

        assertTrue(management.isLoggedIn(userToLogin));

        management.logout(userToLogin);

        assertFalse(management.isLoggedIn(userToLogin));
    }

    @Test
    void retrieveAllUsers() {
        IUserManagement management = getDefaultManagement();

        List<User> allUsers = management.retrieveAllUsers();

        Collections.sort(allUsers);
        assertEquals(getDefaultUsers(), allUsers);

        // check if there are no passwords
        allUsers.forEach(u -> assertEquals("", u.getPassword()));
    }

    @Test
    void updateUnknownUser() {
        IUserManagement management = getDefaultManagement();
        assertThrows(UserManagementException.class, () -> management.updateUser(userNotInStore));
    }

    @Test
    void updateUserPassword_LoggedIn() {
        IUserManagement management = getDefaultManagement();
        User userToUpdate = users.get(0);
        User updatedUser = new UserDTO(userToUpdate.getID(), userToUpdate.getUsername(), "newPassword", null);

        management.login(userToUpdate.getUsername(), userToUpdate.getPassword());
        assertTrue(management.isLoggedIn(userToUpdate));

        management.updateUser(updatedUser);
        assertTrue(management.isLoggedIn(updatedUser));

        management.logout(updatedUser);
        assertFalse(management.isLoggedIn(updatedUser));

        management.login(updatedUser.getUsername(), updatedUser.getPassword());
        assertTrue(management.isLoggedIn(updatedUser));
    }

    @Test
    void updateUserPassword_NotLoggedIn() {
        IUserManagement management = getDefaultManagement();
        User userToUpdate = users.get(0);
        User updatedUser = new UserDTO(userToUpdate.getID(), userToUpdate.getUsername(), "newPassword", null);

        assertFalse(management.isLoggedIn(userToUpdate));
        management.updateUser(updatedUser);

        management.login(updatedUser.getUsername(), updatedUser.getPassword());
        assertTrue(management.isLoggedIn(updatedUser));
    }

    @Test
    void updateUser_Mail() {
        IUserManagement management = getDefaultManagement();
        User userToUpdate = users.get(0);
        User updatedUser = new UserDTO(userToUpdate.getID(), userToUpdate.getUsername(), "", "newMail@mail.com");

        management.updateUser(updatedUser);

        User user = management.login(updatedUser.getUsername(), updatedUser.getPassword());
        assertTrue(management.isLoggedIn(updatedUser));
        assertEquals(updatedUser.getEMail(), user.getEMail());
    }
}
