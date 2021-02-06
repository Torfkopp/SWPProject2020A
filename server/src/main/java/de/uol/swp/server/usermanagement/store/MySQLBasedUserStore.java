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
 * This is the MySQL based user store. The database that holds
 * the user information is persistently stored on a remote
 * server hosted at the ARBI
 *
 * @author Marvin Drees
 * @implNote This store will never return the password of a user!
 * @see AbstractUserStore
 * @see UserStore
 */
public class MySQLBasedUserStore extends AbstractUserStore {

    static final String JDBC_DRIVER = "org.mysql.jdbc.Driver";
    static final String DB_URL = "134.106.11.89:50010";
    static final String USER = "catan";
    static final String PASS = "definitlysecurepassword";
    Connection conn = null;
    PreparedStatement pstmt = null;

    /**
     * This method registers the user with its specific and unique username,
     * password and e-mail and saves it in the database.
     *
     */
    @Override
    public User createUser(String username, String password, String eMail) {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("Username must not be null");
        }

        String passwordHash = hash(password);

        if (findUser(username).isEmpty()) {
            try {
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                conn.setAutoCommit(true);

                String sql = "INSERT INTO USERDB (username, mail, pass) VALUES (?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.setString(2, eMail);
                pstmt.setString(3, passwordHash);
                pstmt.executeUpdate();
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
            return new UserDTO(username, passwordHash, eMail).getWithoutPassword();
        } else {
            throw new IllegalArgumentException("Username must not be taken already");
        }
    }

    /**
     * This method finds and returns the specific user
     * from the database without a password comparison.
     *
     */
    @Override
    public Optional<User> findUser(String username) {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "SELECT * FROM USERDB WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String user = rs.getString("username");
                String mail = rs.getString("mail");
                String pass = rs.getString("pass");

                if (user.equals(username)) {
                    User usr = new UserDTO(user, pass, mail);
                    return Optional.of(usr.getWithoutPassword());
                }
            }
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
        return Optional.empty();
    }

    /**
     * This method searches for a user that matches both
     * the provided username and password and returns a
     * UserDTO for the matching result.
     *
     */
    @Override
    public Optional<User> findUser(String username, String password) {
        String passwordHash = hash(password);

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "SELECT * FROM USERDB WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String user = rs.getString("username");
                String mail = rs.getString("mail");
                String pass = rs.getString("pass");

                if (user.equals(username) && pass.equals(passwordHash)) {
                    User usr = new UserDTO(user, pass, mail);
                    return Optional.of(usr.getWithoutPassword());
                }
            }
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
        return Optional.empty();
    }

    /**
     * This method dumps the whole database and puts
     * the data from each row into a UserDTO which then
     * gets put into a list.
     *
     */
    @Override
    public List<User> getAllUsers() {
        List<User> retUsers = new ArrayList<>();

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "SELECT * FROM USERDB";
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                String mail = rs.getString("mail");
                String pass = rs.getString("pass");

                User usr = new UserDTO(username, pass, mail);
                retUsers.add(usr.getWithoutPassword());
            }
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
        return retUsers;
    }

    /**
     * This method removes the row matching the provided username.
     *
     */
    @Override
    public void removeUser(String username) {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "DELETE FROM USERDB WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.executeUpdate();
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
    }

    /**
     * This method allows the user to change his unique username, password or e-mail.
     * The user will not be able to update his username or e-mail into already registered once.
     *
     */
    @Override
    public User updateUser(String username, String password, String eMail) {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("Username must not be null");
        }

        String passwordHash = hash(password);

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "UPDATE USERDB SET pass = ?, mail = ? WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, passwordHash);
            pstmt.setString(2, eMail);
            pstmt.setString(3, username);
            pstmt.executeUpdate();
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
        return new UserDTO(username, passwordHash, eMail).getWithoutPassword();
    }
}
