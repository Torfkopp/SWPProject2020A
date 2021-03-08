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
 * @since 2021-02-10
 */
public class MySQLBasedUserStore extends AbstractUserStore {

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://134.106.11.89:50010/catan_user_schema";
    static final String USER = "catan";
    static final String PASS = "rNZcEqeiqMJpdr9M";
    Connection conn = null;
    PreparedStatement pstmt = null;
    private int nextID;

    /**
     * This method registers the user with its specific and unique username,
     * password and e-mail and saves it in the database.
     *
     * @author Marvin Drees
     * @since 2021-02-10
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

                String sql = "INSERT INTO userdb (username, mail, pass) VALUES (?, ?, ?)";
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
            Optional<User> usr = findUser(username);
            if (usr.isEmpty()) throw new RuntimeException("Something went wrong when creating the user");
            return usr.get().getWithoutPassword();
        } else {
            throw new IllegalArgumentException("Username must not be taken already");
        }
    }

    /**
     * This method finds and returns the user specified by the provided ID
     * without a password comparison
     *
     * @author Aldin Dervisi
     * @author Phillip-André Suhr
     * @since 2021-02-23
     */
    @Override
    public Optional<User> findUser(int id) {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "SELECT * FROM userdb WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
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
     * This method finds and returns the specific user
     * from the database without a password comparison.
     *
     * @author Marvin Drees
     * @since 2021-02-10
     */
    @Override
    public Optional<User> findUser(String username) {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "SELECT * FROM userdb WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
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
     * @author Marvin Drees
     * @since 2021-02-10
     */
    @Override
    public Optional<User> findUser(String username, String password) {
        String passwordHash = hash(password);

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "SELECT * FROM userdb WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("id");
                String user = rs.getString("username");
                String mail = rs.getString("mail");
                String pass = rs.getString("pass");

                if (user.equals(username) && pass.equals(passwordHash)) {
                    User usr = new UserDTO(userId, user, pass, mail);
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
     * @author Marvin Drees
     * @since 2021-02-10
     */
    @Override
    public List<User> getAllUsers() {
        List<User> retUsers = new ArrayList<>();

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "SELECT * FROM userdb";
            pstmt = conn.prepareStatement(sql);
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
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);
            pstmt = conn.prepareStatement(
                    "SELECT `AUTO_INCREMENT` FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'catan_user_schema' AND TABLE_NAME = 'userdb'");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) nextID = rs.getInt(1);
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
        return nextID;
    }

    /**
     * This method removes the row matching the provided ID.
     *
     * @author Aldin Dervisi
     * @author Phillip-André Suhr
     * @since 2021-02-23
     */
    @Override
    public void removeUser(int id) {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "DELETE FROM userdb WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
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
     * This method removes the row matching the provided username.
     *
     * @author Marvin Drees
     * @since 2021-02-10
     */
    @Override
    public void removeUser(String username) {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "DELETE FROM userdb WHERE username = ?";
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
     * The user will not be able to update his username or e-mail into already registered ones.
     *
     * @author Marvin Drees
     * @since 2021-02-10
     */
    @Override
    public User updateUser(int id, String username, String password, String eMail) {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("Username must not be null");
        }

        String passwordHash = hash(password);

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "UPDATE userdb SET username = ?, pass = ?, mail = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, eMail);
            pstmt.setInt(4, id);
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
        Optional<User> usr = findUser(username);
        if (usr.isPresent()) return usr.get().getWithoutPassword();
        else throw new RuntimeException("Something went wrong when updating the user " + username);
    }

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

            String sql = "UPDATE userdb SET pass = ?, mail = ? WHERE username = ?";
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
        Optional<User> user = findUser(username);
        if (user.isEmpty()) throw new RuntimeException("Something went wrong when updating the user");
        else return user.get().getWithoutPassword();
    }
}
