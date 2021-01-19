package de.uol.swp.server.usermanagement;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.UserStore;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserManagementTest {

    private static final int NO_USERS = 10;
    private static final List<User> users;
    private static final User userNotInStore = new UserDTO("marco" + NO_USERS, "marco" + NO_USERS, "marco" + NO_USERS + "@grawunder.de");

    static {
        users = new ArrayList<>();
        for (int i = 0; i < NO_USERS; i++) {
            users.add(new UserDTO("marco" + i, "marco" + i, "marco" + i + "@grawunder.de"));
        }
        Collections.sort(users);
    }

    List<User> getDefaultUsers() {
        return Collections.unmodifiableList(users);
    }

    ServerUserService getDefaultManagement() {
        UserStore store = new MainMemoryBasedUserStore();
        List<User> users = getDefaultUsers();
        users.forEach(u -> store.createUser(u.getUsername(), u.getPassword(), u.getEMail()));
        return new UserManagement(store);
    }

    @Test
    void loginUser() {
        ServerUserService management = getDefaultManagement();
        User userToLogIn = users.get(0);

        management.login(userToLogIn.getUsername(), userToLogIn.getPassword());

        assertTrue(management.isLoggedIn(userToLogIn));
    }

    @Test
    void loginUserEmptyPassword() {
        ServerUserService management = getDefaultManagement();
        User userToLogIn = users.get(0);

        assertThrows(SecurityException.class, () -> management.login(userToLogIn.getUsername(), ""));

        assertFalse(management.isLoggedIn(userToLogIn));
    }

    @Test
    void loginUserWrongPassword() {
        ServerUserService management = getDefaultManagement();
        User userToLogIn = users.get(0);
        User secondUser = users.get(1);

        assertThrows(SecurityException.class, () -> management.login(userToLogIn.getUsername(), secondUser.getPassword()));

        assertFalse(management.isLoggedIn(userToLogIn));
    }

    @Test
    void logoutUser() {
        ServerUserService management = getDefaultManagement();
        User userToLogin = users.get(0);

        management.login(userToLogin.getUsername(), userToLogin.getPassword());

        assertTrue(management.isLoggedIn(userToLogin));

        management.logout(userToLogin);

        assertFalse(management.isLoggedIn(userToLogin));
    }

    @Test
    void createUser() {
        ServerUserService management = getDefaultManagement();

        management.createUser(userNotInStore);

        // Creation leads not to log in
        assertFalse(management.isLoggedIn(userNotInStore));

        // Only way to test, if user is stored
        management.login(userNotInStore.getUsername(), userNotInStore.getPassword());

        assertTrue(management.isLoggedIn(userNotInStore));
    }

    @Test
    void dropUser() {
        ServerUserService management = getDefaultManagement();
        management.createUser(userNotInStore);

        management.dropUser(userNotInStore);

        assertThrows(SecurityException.class,
                () -> management.login(userNotInStore.getUsername(), userNotInStore.getPassword()));
    }

    @Test
    void dropUserNotExisting() {
        ServerUserService management = getDefaultManagement();
        assertThrows(UserManagementException.class,
                () -> management.dropUser(userNotInStore));
    }

    @Test
    void createUserAlreadyExisting() {
        ServerUserService management = getDefaultManagement();
        User userToCreate = users.get(0);

        assertThrows(UserManagementException.class, () -> management.createUser(userToCreate));
    }

    @Test
    void updateUserPassword_NotLoggedIn() {
        ServerUserService management = getDefaultManagement();
        User userToUpdate = users.get(0);
        User updatedUser = new UserDTO(userToUpdate.getUsername(), "newPassword", null);

        assertFalse(management.isLoggedIn(userToUpdate));
        management.updateUser(updatedUser);

        management.login(updatedUser.getUsername(), updatedUser.getPassword());
        assertTrue(management.isLoggedIn(updatedUser));
    }

    @Test
    void updateUser_Mail() {
        ServerUserService management = getDefaultManagement();
        User userToUpdate = users.get(0);
        User updatedUser = new UserDTO(userToUpdate.getUsername(), "", "newMail@mail.com");

        management.updateUser(updatedUser);

        User user = management.login(updatedUser.getUsername(), updatedUser.getPassword());
        assertTrue(management.isLoggedIn(updatedUser));
        assertEquals(user.getEMail(), updatedUser.getEMail());
    }

    @Test
    void updateUserPassword_LoggedIn() {
        ServerUserService management = getDefaultManagement();
        User userToUpdate = users.get(0);
        User updatedUser = new UserDTO(userToUpdate.getUsername(), "newPassword", null);

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
    void updateUnknownUser() {
        ServerUserService management = getDefaultManagement();
        assertThrows(UserManagementException.class, () -> management.updateUser(userNotInStore));
    }

    @Test
    void retrieveAllUsers() {
        ServerUserService management = getDefaultManagement();

        List<User> allUsers = management.retrieveAllUsers();

        Collections.sort(allUsers);
        assertEquals(allUsers, getDefaultUsers());

        // check if there are no passwords
        // TODO: Normally, there should be no logic in tests
        allUsers.forEach(u -> assertEquals(u.getPassword(), ""));
    }
}
