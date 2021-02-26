package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MySQLBasedUserStoreTest {

    private static final int NO_USERS = 10;
    private static List<User> users;
    private static int nextID;

    { // do not make static (will cause tests to fail)
        UserStore store = new MySQLBasedUserStore();
        MySQLBasedUserStoreTest.nextID = store.getNextUserID();
        MySQLBasedUserStoreTest.users = new ArrayList<>();
        for (int i = nextID; i < NO_USERS + nextID; i++) {
            users.add(new UserDTO(i, "Us3rName" + i, "123GoodPassword" + i, "Username" + i + "@username.de"));
        }
        Collections.sort(users);
    }

    // Set to true if you manually want to run these tests
    // This is only needed to not run these time intensive tests in CI
    static boolean isLocal() {
        return false;
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
    void changePasswordWithIdParameterUpdate() {
        UserStore store = getDefaultStore();
        User userToUpdate = getDefaultUsers().get(2);
        Optional<User> usr = store.findUser(userToUpdate.getID());
        assertTrue(usr.isPresent());
        userToUpdate = usr.get();

        String newPass = userToUpdate.getPassword() + "_NEWPASS";
        store.updateUser(userToUpdate.getID(), userToUpdate.getUsername(), newPass, userToUpdate.getEMail());

        Optional<User> userFound = store.findUser(userToUpdate.getUsername(), newPass);

        assertTrue(userFound.isPresent());
        assertEquals(userFound.get(), userToUpdate);
        assertEquals(userFound.get().getID(), userToUpdate.getID());
        assertEquals(userFound.get().getUsername(), userToUpdate.getUsername());
        assertEquals(userFound.get().getEMail(), userToUpdate.getEMail());
    }

    @Test
    @EnabledIf("isLocal")
    void changePasswordWithNoIdParameterUpdate() {
        UserStore store = getDefaultStore();
        User userToUpdate = getDefaultUsers().get(2);
        Optional<User> usr = store.findUser(userToUpdate.getID());
        assertTrue(usr.isPresent());
        userToUpdate = usr.get();
        String newPass = userToUpdate.getPassword() + "_NEWPASS";

        store.updateUser(userToUpdate.getUsername(), newPass, userToUpdate.getEMail());

        Optional<User> userFound = store.findUser(userToUpdate.getUsername(), newPass);

        assertTrue(userFound.isPresent());
        assertEquals(userFound.get(), userToUpdate);
        assertEquals(userFound.get().getID(), userToUpdate.getID());
        assertEquals(userToUpdate.getUsername(), userFound.get().getUsername());
        assertEquals(userToUpdate.getEMail(), userFound.get().getEMail());
        assertEquals(userFound.get().getPassword(), "");
    }

    @Test
    @EnabledIf("isLocal")
    void createEmptyUser() {
        UserStore store = getDefaultStore();

        assertThrows(IllegalArgumentException.class, () -> store.createUser("", "", ""));
    }

    @Test
    @EnabledIf("isLocal")
    void dropUserById() {
        UserStore store = getDefaultStore();
        User userToRemove = getDefaultUsers().get(3);
        Optional<User> userFound = store.findUser(userToRemove.getID());
        assertTrue(userFound.isPresent());

        store.removeUser(userToRemove.getID());

        userFound = store.findUser(userToRemove.getID());

        assertTrue(userFound.isEmpty());
    }

    @Test
    @EnabledIf("isLocal")
    void dropUserByUsername() {
        UserStore store = getDefaultStore();
        User userToRemove = getDefaultUsers().get(3);

        store.removeUser(userToRemove.getUsername());

        Optional<User> userFound = store.findUser(userToRemove.getUsername());

        assertTrue(userFound.isEmpty());
    }

    @Test
    @EnabledIf("isLocal")
    void findUserById() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(2);

        Optional<User> userFound = store.findUser(userToFind.getID());

        assertTrue(userFound.isPresent());
        assertEquals(userFound.get(), userToFind);
        assertEquals(userFound.get().getID(), userToFind.getID());
        assertEquals(userFound.get().getUsername(), userToFind.getUsername());
        assertEquals(userFound.get().getEMail(), userToFind.getEMail());
    }

    @Test
    @EnabledIf("isLocal")
    void findUserByName() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser(userToFind.getUsername());

        assertTrue(userFound.isPresent());
        assertEquals(userFound.get(), userToFind);
        assertEquals(userFound.get().getID(), userToFind.getID());
        assertEquals(userToFind.getUsername(), userFound.get().getUsername());
        assertEquals(userToFind.getEMail(), userFound.get().getEMail());
        assertEquals(userFound.get().getPassword(), "");
    }

    @Test
    @EnabledIf("isLocal")
    void findUserByNameAndPassword() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser(userToFind.getUsername(), userToFind.getPassword());

        assertTrue(userFound.isPresent());
        assertEquals(userFound.get(), userToFind);
        assertEquals(userFound.get().getID(), userToFind.getID());
        assertEquals(userToFind.getUsername(), userFound.get().getUsername());
        assertEquals(userToFind.getEMail(), userFound.get().getEMail());
        assertEquals(userFound.get().getPassword(), "");
    }

    @Test
    @EnabledIf("isLocal")
    void findUserByNameAndPassword_EmptyUser_NotFound() {
        UserStore store = getDefaultStore();

        Optional<User> userFound = store.findUser(null, "");

        assertTrue(userFound.isEmpty());
    }

    @Test
    @EnabledIf("isLocal")
    void findUserByNameAndPassword_NotFound() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser(userToFind.getUsername(), "");

        assertTrue(userFound.isEmpty());
    }

    @Test
    @EnabledIf("isLocal")
    void findUserByName_NotFound() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser("öööö" + userToFind.getUsername());

        assertTrue(userFound.isEmpty());
    }

    @Test
    @EnabledIf("isLocal")
    void getAllUsers() {
        UserStore store = getDefaultStore();
        List<User> allUsers = getDefaultUsers();

        List<User> allUsersFromStore = store.getAllUsers();
        allUsersFromStore.forEach(u -> assertEquals(u.getPassword(), ""));
        // We can only reliably check the created test users.
        List<User> allTestUsers = new ArrayList<>(
                allUsersFromStore.subList(allUsersFromStore.size() - 10, allUsersFromStore.size()));

        allTestUsers.forEach(u -> assertEquals(u.getPassword(), ""));
        Collections.sort(allTestUsers);

        assertEquals(allUsers, allTestUsers);
        for (int i = 0; i < allUsers.size() && i < allTestUsers.size(); i++) {
            assertEquals(allUsers.get(i).getID(), allTestUsers.get(i).getID());
            assertEquals(allUsers.get(i).getUsername(), allTestUsers.get(i).getUsername());
            assertEquals(allUsers.get(i).getEMail(), allTestUsers.get(i).getEMail());
        }
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
    void updateEmailWithIdParameterUpdate() {
        UserStore store = getDefaultStore();
        User userToUpdate = getDefaultUsers().get(2);
        Optional<User> usr = store.findUser(userToUpdate.getUsername());
        assertTrue(usr.isPresent());
        userToUpdate = usr.get();
        String newEMail = userToUpdate.getEMail() + "@TESTING";

        store.updateUser(userToUpdate.getID(), userToUpdate.getUsername(), userToUpdate.getPassword(), newEMail);

        Optional<User> userFound = store.findUser(userToUpdate.getUsername());

        assertTrue(userFound.isPresent());
        assertEquals(userFound.get(), userToUpdate);
        assertEquals(userFound.get().getID(), userToUpdate.getID());
        assertEquals(userFound.get().getUsername(), userToUpdate.getUsername());
        assertEquals(userFound.get().getEMail(), newEMail);
    }

    @Test
    @EnabledIf("isLocal")
    void updateEmailWithNoIdParameterUpdate() {
        UserStore store = getDefaultStore();
        User userToUpdate = getDefaultUsers().get(2);
        Optional<User> usr = store.findUser(userToUpdate.getUsername());
        assertTrue(usr.isPresent());
        userToUpdate = usr.get();
        String newEMail = userToUpdate.getEMail() + "@TESTING";

        store.updateUser(userToUpdate.getUsername(), userToUpdate.getPassword(), newEMail);

        Optional<User> userFound = store.findUser(userToUpdate.getUsername());

        assertTrue(userFound.isPresent());
        assertEquals(userFound.get(), userToUpdate);
        assertEquals(userFound.get().getID(), userToUpdate.getID());
        assertEquals(userFound.get().getUsername(), userToUpdate.getUsername());
        assertEquals(userFound.get().getEMail(), newEMail);
    }
}