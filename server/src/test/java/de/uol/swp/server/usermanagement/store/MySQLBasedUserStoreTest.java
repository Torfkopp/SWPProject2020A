package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MySQLBasedUserStoreTest {

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://134.106.11.89:50010/catan_user_schema";
    static final String USER = "catan";
    static final String PASS = "rNZcEqeiqMJpdr9M";
    private static final int NO_USERS = 10;
    private static final List<User> users;

    // Set to true if you manually want to run these tests
    // This is only needed to not run these time intensive tests in CI
    static boolean isLocal() {
        return false;
    }

    private static final int LATEST_ID = getLatestID();
    private static final int TEST_USER_START_ID = LATEST_ID + NO_USERS;

    static {
        users = new ArrayList<>();
        for (int i = LATEST_ID; i < TEST_USER_START_ID; i++) {
            users.add(new UserDTO(i, "Us3rName" + i, "123GoodPassword" + i, "Username" + i + "@username.de"));
        }
        Collections.sort(users);
    }

    /**
     * Helper method to find out what ID the next created user will have
     * <p>
     * Used to make comparisons possible. Asks the database about the current
     * value of the AUTO_INCREMENT variable used for user ID assignment in the
     * MySQL database table userdb.
     *
     * @author Aldin Dervisi
     * @author Phillip-André Suhr
     * @since 2021-02-24
     */
    private static int getLatestID() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int currentAutoIncrementValue = -1;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);
            pstmt = conn.prepareStatement(
                    "SELECT `AUTO_INCREMENT` " + "FROM INFORMATION_SCHEMA.TABLES " + "WHERE TABLE_SCHEMA = 'catan_user_schema' " + "AND TABLE_NAME = 'userdb'");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) currentAutoIncrementValue = rs.getInt(1);
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException ignored) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return currentAutoIncrementValue;
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
        Optional<User> usr = store.findUser(userToUpdate.getUsername());
        assertTrue(usr.isPresent());
        userToUpdate = usr.get();

        store.updateUser(userToUpdate.getID(), userToUpdate.getUsername(), userToUpdate.getPassword() + "_NEWPASS",
                         userToUpdate.getEMail());

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
        // Cannot compare against the object or ID because it is unknown at creation of the UserDTO list at the start
        // which ID the users will have as that is solely handled by the store
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
        // Cannot compare against the object or ID because it is unknown at creation of the UserDTO list at the start
        // which ID the users will have as that is solely handled by the store
        assertEquals(userToFind.getUsername(), userFound.get().getUsername());
        assertEquals(userToFind.getEMail(), userFound.get().getEMail());
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

        List<User> storedUsers = store.getAllUsers();
        storedUsers.forEach(u -> assertEquals(u.getPassword(), ""));
        // We can only reliably check the created test users.
        List<User> allTestUsers = new ArrayList<>(storedUsers.subList(storedUsers.size() - 10, storedUsers.size()));

        // Cannot compare against the object or ID because it is unknown at creation of the UserDTO list at the start
        // which ID the users will have as that is solely handled by the store
        // here, we iterate over each list and compare usernames and emails
        for (int i = 0; i < allUsers.size() && i < allTestUsers.size(); i++) {
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
    void updateUser() {
        UserStore store = getDefaultStore();
        User userToUpdate = getDefaultUsers().get(2);
        Optional<User> usr = store.findUser(userToUpdate.getUsername());
        assertTrue(usr.isPresent());
        userToUpdate = usr.get();

        store.updateUser(userToUpdate.getID(), userToUpdate.getUsername(), userToUpdate.getPassword(),
                         userToUpdate.getEMail() + "@TESTING");

        Optional<User> userFound = store.findUser(userToUpdate.getUsername());

        assertTrue(userFound.isPresent());
        assertEquals(userFound.get().getEMail(), userToUpdate.getEMail() + "@TESTING");
    }
}