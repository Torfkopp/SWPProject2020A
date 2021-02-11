package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MySQLBasedUserStoreTest {

    private static final int NO_USERS = 10;
    private static final List<User> users;

    // Set to true if you manually want to run these tests
    // This is only needed to not run these time intensive tests in CI
    static boolean isLocal() {
        return false;
    }

    static {
        users = new ArrayList<>();
        for (int i = 0; i < NO_USERS; i++) {
            users.add(new UserDTO("Us3rName" + i, "123GoodPassword" + i, "Username" + i + "@username.de"));
        }
        Collections.sort(users);
    }

    UserStore getDefaultStore() {
        return new MySQLBasedUserStore();
    }

    List<User> getDefaultUsers() {
        return Collections.unmodifiableList(users);
    }

    @BeforeEach
    void fillDatabase() {
        UserStore store = getDefaultStore();
        List<User> users = getDefaultUsers();
        users.forEach(u -> store.createUser(u.getUsername(), u.getPassword(), u.getEMail()));
    }

    @AfterEach
    void cleanDatabase() {
        UserStore store = getDefaultStore();
        List<User> users = getDefaultUsers();
        users.forEach(u -> store.removeUser(u.getUsername()));
    }

    @Test
    @EnabledIf("isLocal")
    void changePassword() {
        UserStore store = getDefaultStore();
        User userToUpdate = getDefaultUsers().get(2);

        store.updateUser(userToUpdate.getUsername(), userToUpdate.getPassword() + "_NEWPASS", userToUpdate.getEMail());

        Optional<User> userFound = store.findUser(userToUpdate.getUsername(), userToUpdate.getPassword() + "_NEWPASS");

        assertTrue(userFound.isPresent());
        assertEquals(userFound.get().getEMail(), userToUpdate.getEMail());
    }

    @Test
    @EnabledIf("isLocal")
    void createEmptyUser() {
        UserStore store = getDefaultStore();

        assertThrows(IllegalArgumentException.class, () -> store.createUser("", "", ""));
    }

    @Test
    @EnabledIf("isLocal")
    void dropUser() {
        UserStore store = getDefaultStore();
        User userToRemove = getDefaultUsers().get(3);

        store.removeUser(userToRemove.getUsername());

        Optional<User> userFound = store.findUser(userToRemove.getUsername());

        assertFalse(userFound.isPresent());
    }

    @Test
    @EnabledIf("isLocal")
    void findUserByName() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser(userToFind.getUsername());

        assertTrue(userFound.isPresent());
        assertEquals(userToFind, userFound.get());
        assertEquals(userFound.get().getPassword(), "");
    }

    @Test
    @EnabledIf("isLocal")
    void findUserByNameAndPassword() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser(userToFind.getUsername(), userToFind.getPassword());

        assertTrue(userFound.isPresent());
        assertEquals(userToFind, userFound.get());
        assertEquals(userFound.get().getPassword(), "");
    }

    @Test
    @EnabledIf("isLocal")
    void findUserByNameAndPassword_EmptyUser_NotFound() {
        UserStore store = getDefaultStore();

        Optional<User> userFound = store.findUser(null, "");

        assertFalse(userFound.isPresent());
    }

    @Test
    @EnabledIf("isLocal")
    void findUserByNameAndPassword_NotFound() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser(userToFind.getUsername(), "");

        assertFalse(userFound.isPresent());
    }

    @Test
    @EnabledIf("isLocal")
    void findUserByName_NotFound() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser("öööö" + userToFind.getUsername());

        assertFalse(userFound.isPresent());
    }

    @Test
    @EnabledIf("isLocal")
    void getAllUsers() {
        UserStore store = getDefaultStore();
        List<User> allUsers = getDefaultUsers();

        List<User> allUsersFromStore = store.getAllUsers();

        allUsersFromStore.forEach(u -> assertEquals(u.getPassword(), ""));
        Collections.sort(allUsersFromStore);
        assertEquals(allUsers, allUsersFromStore);
    }

    @Test
    @EnabledIf("isLocal")
    void overwriteUser() {
        UserStore store = getDefaultStore();
        User userToCreate = getDefaultUsers().get(1);

        assertThrows(IllegalArgumentException.class, () -> store
                .createUser(userToCreate.getUsername(), userToCreate.getPassword(), userToCreate.getEMail()));
    }

    @Test
    @EnabledIf("isLocal")
    void updateUser() {
        UserStore store = getDefaultStore();
        User userToUpdate = getDefaultUsers().get(2);

        store.updateUser(userToUpdate.getUsername(), userToUpdate.getPassword(), userToUpdate.getEMail() + "@TESTING");

        Optional<User> userFound = store.findUser(userToUpdate.getUsername());

        assertTrue(userFound.isPresent());
        assertEquals(userFound.get().getEMail(), userToUpdate.getEMail() + "@TESTING");
    }
}