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
    private static final List<User> users;

    static {
        users = new ArrayList<>();
        for (int i = 0; i < NO_USERS; i++) {
            users.add(new UserDTO(i, "Us3rName" + i, "123GoodPassword" + i, "Username" + i + "@username.de"));
        }
        Collections.sort(users);
    }

    UserStore getDefaultStore() {
        return new H2BasedUserStore();
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
    void changePasswordWithIdParameterUpdate() {
        UserStore store = getDefaultStore();
        User userToUpdate = getDefaultUsers().get(2);
        Optional<User> usr = store.findUser(userToUpdate.getUsername());
        assertTrue(usr.isPresent());
        userToUpdate = usr.get();

        store.updateUser(userToUpdate.getID(), userToUpdate.getUsername(), userToUpdate.getPassword() + "_NEWPASS",
                         userToUpdate.getEMail());

        Optional<User> userFound = store.findUser(userToUpdate.getUsername(), userToUpdate.getPassword() + "_NEWPASS");

        assertTrue(userFound.isPresent());
        assertEquals(userFound.get(), userToUpdate);
        assertEquals(userFound.get().getID(), userToUpdate.getID());
        assertEquals(userFound.get().getUsername(), userToUpdate.getUsername());
        assertEquals(userFound.get().getEMail(), userToUpdate.getEMail());
    }

    @Test
    void changePasswordWithNoIdParameterUpdate() {
        UserStore store = getDefaultStore();
        User userToUpdate = getDefaultUsers().get(2);
        Optional<User> usr = store.findUser(userToUpdate.getUsername());
        assertTrue(usr.isPresent());
        userToUpdate = usr.get();

        store.updateUser(userToUpdate.getUsername(), userToUpdate.getPassword() + "_NEWPASS", userToUpdate.getEMail());

        Optional<User> userFound = store.findUser(userToUpdate.getUsername(), userToUpdate.getPassword() + "_NEWPASS");

        assertTrue(userFound.isPresent());
        assertEquals(userFound.get(), userToUpdate);
        assertEquals(userFound.get().getID(), userToUpdate.getID());
        assertEquals(userFound.get().getUsername(), userToUpdate.getUsername());
        assertEquals(userFound.get().getEMail(), userToUpdate.getEMail());
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

        store.removeUser(userToRemove.getID());

        Optional<User> userFound = store.findUser(userToRemove.getID());

        assertFalse(userFound.isPresent());
    }

    @Test
    void dropUserByUsername() {
        UserStore store = getDefaultStore();
        User userToRemove = getDefaultUsers().get(3);

        store.removeUser(userToRemove.getUsername());

        Optional<User> userFound = store.findUser(userToRemove.getUsername());

        assertFalse(userFound.isPresent());
    }

    @Test
    void findUserByName() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser(userToFind.getUsername());

        assertTrue(userFound.isPresent());
        // Cannot compare against the object or ID because it is unknown at creation of the UserDTO list at the start
        // which ID the users will have as that is solely handled by the store
        assertEquals(userToFind.getUsername(), userFound.get().getUsername());
        assertEquals(userToFind.getEMail(), userFound.get().getEMail());
        assertEquals(userFound.get().getPassword(), "");
    }

    @Test
    void findUserByNameAndPassword() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser(userToFind.getUsername(), userToFind.getPassword());

        assertTrue(userFound.isPresent());
        // Cannot compare against the object or ID because it is unknown at creation of the UserDTO list at the start
        // which ID the users will have as that is solely handled by the store
        assertEquals(userToFind.getUsername(), userFound.get().getUsername());
        assertEquals(userToFind.getEMail(), userFound.get().getEMail());
        assertEquals(userFound.get().getPassword(), "");
    }

    @Test
    void findUserByNameAndPassword_EmptyUser_NotFound() {
        UserStore store = getDefaultStore();

        Optional<User> userFound = store.findUser(null, "");

        assertFalse(userFound.isPresent());
    }

    @Test
    void findUserByNameAndPassword_NotFound() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser(userToFind.getUsername(), "");

        assertFalse(userFound.isPresent());
    }

    @Test
    void findUserByName_NotFound() {
        UserStore store = getDefaultStore();
        User userToFind = getDefaultUsers().get(0);

        Optional<User> userFound = store.findUser("öööö" + userToFind.getUsername());

        assertFalse(userFound.isPresent());
    }

    @Test
    void getAllUsers() {
        UserStore store = getDefaultStore();
        List<User> allUsers = getDefaultUsers();

        List<User> allUsersFromStore = store.getAllUsers();

        allUsersFromStore.forEach(u -> assertEquals(u.getPassword(), ""));
        Collections.sort(allUsersFromStore);

        // Cannot compare against the object or ID because it is unknown at creation of the UserDTO list at the start
        // which ID the users will have as that is solely handled by the store
        // here, we iterate over each list and compare usernames and emails
        for (int i = 0; i < allUsers.size() && i < allUsersFromStore.size(); i++) {
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

        store.updateUser(userToUpdate.getID(), userToUpdate.getUsername(), userToUpdate.getPassword(),
                         userToUpdate.getEMail() + "@TESTING");

        Optional<User> userFound = store.findUser(userToUpdate.getUsername());

        assertTrue(userFound.isPresent());
        assertEquals(userFound.get(), userToUpdate);
        assertEquals(userFound.get().getID(), userToUpdate.getID());
        assertEquals(userFound.get().getUsername(), userToUpdate.getUsername());
        assertEquals(userFound.get().getEMail(), userToUpdate.getEMail() + "@TESTING");
    }

    @Test
    void updateEmailWithNoIdParameterUpdate() {
        UserStore store = getDefaultStore();
        User userToUpdate = getDefaultUsers().get(2);

        Optional<User> usr = store.findUser(userToUpdate.getUsername());
        assertTrue(usr.isPresent());
        userToUpdate = usr.get();

        store.updateUser(userToUpdate.getUsername(), userToUpdate.getPassword(), userToUpdate.getEMail() + "@TESTING");

        Optional<User> userFound = store.findUser(userToUpdate.getUsername());

        assertTrue(userFound.isPresent());
        assertEquals(userFound.get(), userToUpdate);
        assertEquals(userFound.get().getID(), userToUpdate.getID());
        assertEquals(userFound.get().getUsername(), userToUpdate.getUsername());
        assertEquals(userFound.get().getEMail(), userToUpdate.getEMail() + "@TESTING");
    }
}
