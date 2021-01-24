package de.uol.swp.server.usermanagement.store;

import com.google.common.base.Strings;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This is a user store.
 * <p>
 * This is a H2 based in-memory user store that replaced the previous one. The
 * user accounts in this user store only reside within the RAM of your computer
 * and only for as long as the server is running. Therefore, the users have to be
 * added every time the server is started.
 *
 * @author Aldin Dervisi
 * @author Marvin Drees
 * @implNote This store will never return the password of a user!
 * @see de.uol.swp.server.usermanagement.store.AbstractUserStore
 * @see de.uol.swp.server.usermanagement.store.UserStore
 * @since 2020-01-20
 */
public class H2BasedUserStore extends AbstractUserStore implements UserStore {

    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:mem:userdb;DB_CLOSE_DELAY=-1;mode=MySQL";
    static final String USER = "H2";
    static final String PASS = "123456";
    Connection conn = null;
    Statement stmt = null;

    @Override
    public void createTable() {
        try {
            Class.forName(JDBC_DRIVER);

            System.out.println("Connection to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            System.out.println("Creating table in given database...");
            stmt = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS USERDB (" +
                    "username VARCHAR(255), " +
                    "mail VARCHAR(255), " +
                    "pass VARCHAR(255), " +
                    "PRIMARY KEY (username))";
            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");

            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException sqle2) {
                sqle2.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        System.out.println("Database closed.");
    }

    @Override
    public Optional<User> findUser(String username, String password) {

        String passwordHash = hash(password);

        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            // STEP 3: Execute a query
            System.out.println("Connected database successfully...");
            stmt = conn.createStatement();
            String sql = "SELECT username, mail, pass FROM USERDB";
            ResultSet rs = stmt.executeQuery(sql);

            // STEP 4: Extract data from result set
            while (rs.next()) {
                // Retrieve by column name
                String user = rs.getString("username");
                String mail = rs.getString("mail");
                String pass = rs.getString("pass");

                if (user.equals(username) && pass.equals(passwordHash)) {
                    User usr = new UserDTO(user, pass, mail);
                    return Optional.of(usr.getWithoutPassword());
                }

                // Display values
                System.out.print(", Username: " + user);
                System.out.print(", Mail: " + mail);
                System.out.println(", Pass: " + pass);
            }
            // STEP 5: Clean-up environment
            rs.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try

        return Optional.empty();
    }

    @Override
    public Optional<User> findUser(String username) {

        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            // STEP 3: Execute a query
            System.out.println("Connected database successfully...");
            stmt = conn.createStatement();
            String sql = "SELECT username, mail, pass FROM USERDB";
            ResultSet rs = stmt.executeQuery(sql);

            // STEP 4: Extract data from result set
            while (rs.next()) {
                // Retrieve by column name
                String user = rs.getString("username");
                String mail = rs.getString("mail");
                String pass = rs.getString("pass");

                if (user.equals(username)) {
                    User usr = new UserDTO(user, pass, mail);
                    return Optional.of(usr.getWithoutPassword());
                }

                // Display values
                System.out.print(", Username: " + user);
                System.out.print(", Mail: " + mail);
                System.out.println(", Pass: " + pass);
            }
            // STEP 5: Clean-up environment
            rs.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try

        return Optional.empty();
    }

    @Override
    public User createUser(String username, String password, String eMail) {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("Username must not be null");
        }

        String passwordHash = hash(password);

        try {
            Class.forName(JDBC_DRIVER);

            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);
            System.out.println("Connected database successfully...");

            stmt = conn.createStatement();
            String sql = "INSERT INTO USERDB " +
                    "VALUES ('" +
                    username + "', '" +
                    eMail + "', '" +
                    passwordHash +
                    "') ON DUPLICATE KEY UPDATE username = '" + username + "'," +
                    "mail = '" + eMail + "', pass='" + passwordHash + "'";
            stmt.executeUpdate(sql);
            System.out.println("Inserted records into the table...");

            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try
        System.out.println("Connection closed.");

        User usr = new UserDTO(username, passwordHash, eMail);
        return usr;
    }

    @Override
    public User updateUser(String username, String password, String eMail) {

        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("Username must not be null");
        }

        String passwordHash = hash(password);

        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // STEP 2: Open a connection
            System.out.println("Connecting to a database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            // STEP 3: Execute a query
            System.out.println("Connected database successfully...");
            stmt = conn.createStatement();
            String sql = "UPDATE USERDB " + "SET pass = '" + passwordHash + "' WHERE username = '" + username + "'";
            stmt.executeUpdate(sql);
            String sql2 = "UPDATE USERDB " + "SET mail = '" + eMail + "' WHERE username = '" + username + "'";
            stmt.executeUpdate(sql2);
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try

        User usr = new UserDTO(username, passwordHash, eMail);
        return usr;
    }

    @Override
    public void removeUser(String username) {

        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            // STEP 3: Execute a query
            System.out.println("Creating table in given database...");
            stmt = conn.createStatement();
            String sql = "DELETE FROM USERDB WHERE username = '" + username + "'";
            stmt.executeUpdate(sql);
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try
        System.out.println("Connection closed.");
    }

    @Override
    public List<User> getAllUsers() {
        List<User> retUsers = new ArrayList<>();

        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            // STEP 3: Execute a query
            System.out.println("Connected database successfully...");
            stmt = conn.createStatement();
            String sql = "SELECT username, mail, pass FROM USERDB";
            ResultSet rs = stmt.executeQuery(sql);

            // STEP 4: Extract data from result set
            while (rs.next()) {
                // Retrieve by column name
                String username = rs.getString("username");
                String mail = rs.getString("mail");
                String pass = rs.getString("pass");

                User usr = new UserDTO(username, pass, mail);
                retUsers.add(usr.getWithoutPassword());

                // Display values
                System.out.print(", Username: " + username);
                System.out.print(", Mail: " + mail);
                System.out.println(", Pass: " + pass);
            }
            // STEP 5: Clean-up environment
            rs.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try

        return retUsers;
    }
}
