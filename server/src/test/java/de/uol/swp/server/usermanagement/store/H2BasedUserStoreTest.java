package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class H2BasedUserStoreTest {

    private static final int NO_USERS = 10;
    private static int nextID;
    private static List<User> users;

    { // do not make static (will cause tests to fail)
        UserStore store = new H2BasedUserStore();
        H2BasedUserStoreTest.nextID = store.getNextUserID();
        H2BasedUserStoreTest.users = new ArrayList<>();
        for (int i = nextID; i < NO_USERS + nextID; i++) {
            users.add(new UserDTO(i, "Us3rName" + i, "123GoodPassword" + i, "Username" + i + "@username.de"));
        }
        Collections.sort(users);
    }

    @AfterEach
    protected void cleanDatabase() {
        UserStore store = getDefaultStore();
        List<User> users = getDefaultUsers();
        users.forEach(u -> store.removeUser(u.getUsername()));
    }

    @BeforeEach
    protected void fillDatabase() {
        UserStore store = getDefaultStore();
        List<User> users = getDefaultUsers();
        users.forEach(u -> store.createUser(u.getUsername(), u.getPassword(), u.getEMail()));
    }

    protected UserStore getDefaultStore() {
        return new H2BasedUserStore();
    }

    protected List<User> getDefaultUsers() {
        return Collections.unmodifiableList(users);
    }

    @Test
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
        assertEquals(userToUpdate, userFound.get());
        assertEquals(userToUpdate.getID(), userFound.get().getID());
        assertEquals(userToUpdate.getUsername(), userFound.get().getUsername());
        assertEquals(userToUpdate.getEMail(), userFound.get().getEMail());
    }

    @Test
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
        assertEquals(userToUpdate, userFound.get());
        assertEquals(userToUpdate.getID(), userFound.get().getID());
        assertEquals(userToUpdate.getUsername(), userFound.get().getUsername());
        assertEquals(userToUpdate.getEMail(), userFound.get().getEMail());
        assertEquals("", userFound.get().getPassword());
    }

    @Test
    void createEmptyUser() {
        UserStore store = getDefaultStore();

        assertThrows(IllegalArgumentException.class, () -> store.createUser("", "", ""));
    }

    @Test
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
    void dropUserByUsername() {
        UserStore store = getDefaultStore();
        User userToRemove = getDefaultUsers().get(3);

        store.removeUser(userToRemove.getUsername());

        Optional<User> userFound = store.findUser(userToRemove.getUsername());

        assertTrue(userFound.isEmpty());
    }

    @Test
    void findUserById() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(2);

        Optional<User> userFound = store.findUser(userToFind.getID());

        assertTrue(userFound.isPresent());
        assertEquals(userToFind, userFound.get());
        assertEquals(userToFind.getID(), userFound.get().getID());
        assertEquals(userToFind.getUsername(), userFound.get().getUsername());
        assertEquals(userToFind.getEMail(), userFound.get().getEMail());
    }

    @Test
    void findUserByName() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser(userToFind.getUsername());

        assertTrue(userFound.isPresent());
        assertEquals(userToFind, userFound.get());
        assertEquals(userToFind.getID(), userFound.get().getID());
        assertEquals(userToFind.getUsername(), userFound.get().getUsername());
        assertEquals(userToFind.getEMail(), userFound.get().getEMail());
        assertEquals("", userFound.get().getPassword());
    }

    @Test
    void findUserByNameAndPassword() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser(userToFind.getUsername(), userToFind.getPassword());

        assertTrue(userFound.isPresent());
        assertEquals(userToFind, userFound.get());
        assertEquals(userToFind.getID(), userFound.get().getID());
        assertEquals(userToFind.getUsername(), userFound.get().getUsername());
        assertEquals(userToFind.getEMail(), userFound.get().getEMail());
        assertEquals("", userFound.get().getPassword());
    }

    @Test
    void findUserByNameAndPassword_EmptyUser_NotFound() {
        UserStore store = getDefaultStore();

        Optional<User> userFound = store.findUser(null, "");

        assertTrue(userFound.isEmpty());
    }

    @Test
    void findUserByNameAndPassword_NotFound() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser(userToFind.getUsername(), "");

        assertTrue(userFound.isEmpty());
    }

    @Test
    void findUserByName_NotFound() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser("öööö" + userToFind.getUsername());

        assertTrue(userFound.isEmpty());
    }

    @Test
    void getAllUsers() {
        UserStore store = getDefaultStore();
        List<User> allUsers = getDefaultUsers();

        List<User> allUsersFromStore = store.getAllUsers();

        allUsersFromStore.forEach(u -> assertEquals("", u.getPassword()));
        Collections.sort(allUsersFromStore);

        assertEquals(allUsers, allUsersFromStore);
        for (int i = 0; i < allUsers.size() && i < allUsersFromStore.size(); i++) {
            assertEquals(allUsers.get(i).getID(), allUsersFromStore.get(i).getID());
            assertEquals(allUsers.get(i).getUsername(), allUsersFromStore.get(i).getUsername());
            assertEquals(allUsers.get(i).getEMail(), allUsersFromStore.get(i).getEMail());
        }
    }

    @Test
    void overwriteUser() {
        UserStore store = getDefaultStore();
        User userToCreate = getDefaultUsers().get(1);

        assertThrows(IllegalArgumentException.class, () -> store
                .createUser(userToCreate.getUsername(), userToCreate.getPassword(), userToCreate.getEMail()));
    }

    @Test
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
        assertEquals(userToUpdate, userFound.get());
        assertEquals(userToUpdate.getID(), userFound.get().getID());
        assertEquals(userToUpdate.getUsername(), userFound.get().getUsername());
        assertEquals(newEMail, userFound.get().getEMail());
    }

    @Test
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
        assertEquals(userToUpdate, userFound.get());
        assertEquals(userToUpdate.getID(), userFound.get().getID());
        assertEquals(userToUpdate.getUsername(), userFound.get().getUsername());
        assertEquals(newEMail, userFound.get().getEMail());
    }
}
