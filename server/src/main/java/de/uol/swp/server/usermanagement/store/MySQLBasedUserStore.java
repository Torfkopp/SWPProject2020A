package de.uol.swp.server.usermanagement.store;

import com.google.common.base.Strings;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
 * @see IUserStore
 * @since 2021-02-10
 */
public class MySQLBasedUserStore implements IUserStore {

    private static final Logger LOG = LogManager.getLogger(H2BasedUserStore.class);
    private static final String DB_URL = "jdbc:mysql://134.106.11.89:50010/catan_user_schema";
    private static final String USER = "catan";
    private static final String PASS = "rNZcEqeiqMJpdr9M";
    private static Connection conn = null;
    private PreparedStatement pstmt = null;
    private int nextID;

    @Override
    public User createUser(String username, String password, String eMail) throws RuntimeException {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("Username must not be null");
        }

        if (findUser(username).isEmpty()) {
            try {
                conn = openConnection();
                pstmt = conn.prepareStatement("INSERT INTO USERDB (username, mail, pass) VALUES (?, ?, ?)");
                pstmt.setString(1, username);
                pstmt.setString(2, eMail);
                pstmt.setString(3, password);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                LOG.fatal(e.getMessage());
            } finally {
                closeConnection(conn, pstmt);
            }
            Optional<User> usr = findUser(username);
            if (usr.isPresent()) return usr.get().getWithoutPassword();
            else throw new RuntimeException("Something went wrong when creating the user");
        } else {
            throw new IllegalArgumentException("Username must not be taken already");
        }
    }

    @Override
    public Optional<User> findUser(int id) {
        try {
            conn = openConnection();
            pstmt = conn.prepareStatement("SELECT * FROM USERDB WHERE id = ?");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("id");
                String user = rs.getString("username");
                String mail = rs.getString("mail");
                String pass = rs.getString("pass");

                if (userId == id) {
                    User usr = new UserDTO(userId, user, pass, mail);
                    return Optional.of(usr.getWithoutPassword());
                }
            }
            rs.close();
        } catch (SQLException e) {
            LOG.fatal(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findUser(String username) {
        try {
            conn = openConnection();
            pstmt = conn.prepareStatement("SELECT * FROM USERDB WHERE username = ?");
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("id");
                String user = rs.getString("username");
                String mail = rs.getString("mail");
                String pass = rs.getString("pass");

                if (user.equals(username)) {
                    User usr = new UserDTO(userId, user, pass, mail);
                    return Optional.of(usr.getWithoutPassword());
                }
            }
            rs.close();
        } catch (SQLException e) {
            LOG.fatal(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findUser(String username, String password) {
        try {
            conn = openConnection();
            pstmt = conn.prepareStatement("SELECT * FROM USERDB WHERE username = ?");
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("id");
                String user = rs.getString("username");
                String mail = rs.getString("mail");
                String pass = rs.getString("pass");

                if (user.equals(username) && pass.equals(password)) {
                    User usr = new UserDTO(userId, user, pass, mail);
                    return Optional.of(usr.getWithoutPassword());
                }
            }
            rs.close();
        } catch (SQLException e) {
            LOG.fatal(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
        return Optional.empty();
    }

    @Override
    public List<User> getAllUsers() {
        List<User> retUsers = new ArrayList<>();

        try {
            conn = openConnection();
            pstmt = conn.prepareStatement("SELECT * FROM USERDB");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("id");
                String username = rs.getString("username");
                String mail = rs.getString("mail");
                String pass = rs.getString("pass");

                User usr = new UserDTO(userId, username, pass, mail);
                retUsers.add(usr.getWithoutPassword());
            }
            rs.close();
        } catch (SQLException e) {
            LOG.fatal(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
        return retUsers;
    }

    /**
     * This method gets the value that will be assigned to the NEXT created user
     * by looking up the current value of the AUTO_INCREMENT field in the MySQL
     * database.
     *
     * @author Aldin Dervisi
     * @author Phillip-André Suhr
     * @since 2021-02-26
     */
    @Override
    public int getNextUserID() {
        try {
            conn = openConnection();
            pstmt = conn.prepareStatement(
                    "SELECT `AUTO_INCREMENT` FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'catan_user_schema' AND TABLE_NAME = 'userdb'");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) nextID = rs.getInt(1);
            rs.close();
        } catch (SQLException e) {
            LOG.fatal(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
        return nextID;
    }

    @Override
    public void removeUser(int id) {
        try {
            conn = openConnection();
            pstmt = conn.prepareStatement("DELETE FROM USERDB WHERE id = ?");
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOG.fatal(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
    }

    @Override
    public void removeUser(String username) {
        try {
            conn = openConnection();
            pstmt = conn.prepareStatement("DELETE FROM USERDB WHERE username = ?");
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOG.fatal(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
    }

    @Override
    public User updateUser(int id, String username, String password, String eMail) throws RuntimeException {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("Username must not be null");
        }

        Optional<User> user = findUser(username);
        if (user.isPresent() && user.get().getID() != id) throw new IllegalArgumentException("Username already taken");

        try {
            conn = openConnection();
            pstmt = conn.prepareStatement("UPDATE USERDB SET username = ?, pass = ?, mail = ? WHERE id = ?");
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, eMail);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOG.fatal(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
        Optional<User> usr = findUser(username);
        if (usr.isPresent()) return usr.get().getWithoutPassword();
        else throw new RuntimeException("Something went wrong when updating the user " + username);
    }

    @Override
    public User updateUser(String username, String password, String eMail) throws RuntimeException {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("Username must not be null");
        }

        try {
            conn = openConnection();
            pstmt = conn.prepareStatement("UPDATE USERDB SET pass = ?, mail = ? WHERE username = ?");
            pstmt.setString(1, password);
            pstmt.setString(2, eMail);
            pstmt.setString(3, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOG.fatal(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
        Optional<User> user = findUser(username);
        if (user.isEmpty()) throw new RuntimeException("Something went wrong when updating the user");
        else return user.get().getWithoutPassword();
    }

    /**
     * Helper method to close a provided connection
     * and SQL statement.
     *
     * @param conn  The connection to be closed
     * @param pstmt The statement to be closed
     *
     * @author Marvin Drees
     * @since 2021-07-02
     */
    private void closeConnection(Connection conn, PreparedStatement pstmt) {
        try {
            if (pstmt != null) pstmt.close();
        } catch (SQLException e) {
            LOG.fatal(e.getMessage());
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            LOG.fatal(e.getMessage());
        }
    }

    /**
     * Helper method to open a connection to the SQL database.
     *
     * @return The opened connection
     *
     * @throws SQLException Exception when something goes wrong opening the connection
     * @author Marvin Drees
     * @since 2021-07-02
     */
    private Connection openConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
        conn.setAutoCommit(true);
        return conn;
    }
}
