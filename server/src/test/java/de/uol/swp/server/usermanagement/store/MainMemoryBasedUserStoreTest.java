package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MainMemoryBasedUserStoreTest {

    private static final int NO_USERS = 10;
    private static List<User> users;
    private static int nextID;

    { // do not make static (will cause tests to fail)
        IUserStore store = new MainMemoryBasedUserStore();
        MainMemoryBasedUserStoreTest.nextID = store.getNextUserID();
        MainMemoryBasedUserStoreTest.users = new ArrayList<>();
        for (int i = 0; i < NO_USERS + nextID; i++) {
            users.add(new UserDTO(i, "marco" + i, "marco" + i, "marco" + i + "@grawunder.de"));
        }
        Collections.sort(users);
    }

    protected IUserStore getDefaultStore() {
        IUserStore store = new MainMemoryBasedUserStore();
        List<User> users = getDefaultUsers();
        users.forEach(u -> store.createUser(u.getUsername(), u.getPassword(), u.getEMail()));
        return store;
    }

    protected List<User> getDefaultUsers() {
        return Collections.unmodifiableList(users);
    }

    @Test
    void changePasswordWithIdParameterUpdate() {
        IUserStore store = getDefaultStore();
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
        IUserStore store = getDefaultStore();
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
        IUserStore store = getDefaultStore();

        assertThrows(IllegalArgumentException.class, () -> store.createUser("", "", ""));
    }

    @Test
    void dropUserById() {
        IUserStore store = getDefaultStore();
        User userToRemove = getDefaultUsers().get(3);
        Optional<User> userFound = store.findUser(userToRemove.getID());
        assertTrue(userFound.isPresent());

        store.removeUser(userToRemove.getID());

        userFound = store.findUser(userToRemove.getID());

        assertTrue(userFound.isEmpty());
    }

    @Test
    void dropUserByUsername() {
        IUserStore store = getDefaultStore();
        User userToRemove = getDefaultUsers().get(3);

        store.removeUser(userToRemove.getUsername());

        Optional<User> userFound = store.findUser(userToRemove.getUsername());

        assertTrue(userFound.isEmpty());
    }

    @Test
    void findUserById() {
        IUserStore store = getDefaultStore();
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
        // arrange
        IUserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        // act
        Optional<User> userFound = store.findUser(userToFind.getUsername());

        // assert
        assertTrue(userFound.isPresent());
        assertEquals(userToFind, userFound.get());
        assertEquals(userToFind.getID(), userFound.get().getID());
        assertEquals(userToFind.getUsername(), userFound.get().getUsername());
        assertEquals(userToFind.getEMail(), userFound.get().getEMail());
        assertEquals("", userFound.get().getPassword());
    }

    @Test
    void findUserByNameAndPassword() {
        IUserStore store = getDefaultStore();
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
        IUserStore store = getDefaultStore();

        Optional<User> userFound = store.findUser(null, "");

        assertTrue(userFound.isEmpty());
    }

    @Test
    void findUserByNameAndPassword_NotFound() {
        IUserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser(userToFind.getUsername(), "");

        assertTrue(userFound.isEmpty());
    }

    @Test
    void findUserByName_NotFound() {
        IUserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser("öööö" + userToFind.getUsername());

        assertTrue(userFound.isEmpty());
    }

    @Test
    void getAllUsers() {
        IUserStore store = getDefaultStore();
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
        IUserStore store = getDefaultStore();
        User userToCreate = getDefaultUsers().get(1);
        store.createUser(userToCreate.getUsername(), userToCreate.getPassword(), userToCreate.getEMail());
        store.createUser(userToCreate.getUsername(), userToCreate.getPassword(), userToCreate.getEMail());

        Optional<User> userFound = store.findUser(userToCreate.getUsername(), userToCreate.getPassword());

        assertEquals(NO_USERS, store.getAllUsers().size());
        assertTrue(userFound.isPresent());
        assertEquals(userToCreate, userFound.get());
        assertEquals(userToCreate.getID(), userFound.get().getID());
        assertEquals(userToCreate.getUsername(), userFound.get().getUsername());
        assertEquals(userToCreate.getEMail(), userFound.get().getEMail());
    }

    @Test
    void updateEmailWithIdParameterUpdate() {
        IUserStore store = getDefaultStore();
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
        IUserStore store = getDefaultStore();
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
