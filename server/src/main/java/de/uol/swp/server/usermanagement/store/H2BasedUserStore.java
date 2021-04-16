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
 * @since 2021-01-20
 */
public class H2BasedUserStore extends AbstractUserStore {

    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:mem:userdb;DB_CLOSE_DELAY=-1;mode=MySQL";
    static final String USER = "H2";
    static final String PASS = "123456";
    Connection conn = null;
    PreparedStatement pstmt = null;
    private int nextID;

    /**
     * This method registers the user with its specific and unique username,
     * password and e-mail and saves it in the H2 Database.
     */
    @Override
    public User createUser(String username, String password, String eMail) throws RuntimeException {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("Username must not be null");
        }

        createTable();

        if (findUser(username).isEmpty()) {
            try {
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                conn.setAutoCommit(true);

                String sql = "INSERT INTO USERDB (username, mail, pass) VALUES (?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.setString(2, eMail);
                pstmt.setString(3, password);
                pstmt.executeUpdate();
            } catch (ClassNotFoundException | SQLException e) {
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
            else throw new RuntimeException("Something went wrong when creating the user");
        } else {
            throw new IllegalArgumentException("Username must not be taken already");
        }
    }

    /**
     * This method finds and returns the specific user
     * identified by the provided ID from the database
     * without the password.
     *
     * @author Aldin Dervisi
     * @author Phillip-André Suhr
     * @since 2021-02-23
     */
    @Override
    public Optional<User> findUser(int id) {
        createTable();

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "SELECT * FROM USERDB WHERE id = ?";
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
        } catch (ClassNotFoundException | SQLException e) {
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
     * from the database without the password.
     */
    @Override
    public Optional<User> findUser(String username) {
        createTable();

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "SELECT * FROM USERDB WHERE username = ?";
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
        } catch (ClassNotFoundException | SQLException e) {
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
     */
    @Override
    public Optional<User> findUser(String username, String password) {
        createTable();

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "SELECT * FROM USERDB WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
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
        } catch (ClassNotFoundException | SQLException e) {
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
     */
    @Override
    public List<User> getAllUsers() {
        createTable();

        List<User> retUsers = new ArrayList<>();

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "SELECT * FROM USERDB";
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
        } catch (ClassNotFoundException | SQLException e) {
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
     * by looking up the Sequence responsible for the auto_increment id column,
     * fetching its current value, and adding 1 to it.
     *
     * @author Aldin Dervisi
     * @author Phillip-André Suhr
     * @since 2021-02-26
     */
    @Override
    public int getNextUserID() {
        createTable();

        String sequenceName = "";
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "SELECT SEQUENCE_NAME FROM USERDB.INFORMATION_SCHEMA.SEQUENCES LIMIT 1";
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) sequenceName = rs.getString(1);
            rs.close();
            pstmt.close();
            sql = "SELECT CURRENT_VALUE FROM USERDB.INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_NAME = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sequenceName);
            rs = pstmt.executeQuery();
            while (rs.next()) nextID = rs.getInt(1) + 1;
            rs.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException ignored) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException ignored) {
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
        createTable();

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "DELETE FROM USERDB WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
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
     */
    @Override
    public void removeUser(String username) {
        createTable();

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "DELETE FROM USERDB WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
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
     * This method allows the user to change their unique username, password
     * or e-mail.
     * <p>
     * The user will not be able to update his username or e-mail into already
     * registered ones.
     */
    @Override
    public User updateUser(int id, String username, String password, String eMail) {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("Username must not be null");
        }

        createTable();

        Optional<User> usr = findUser(username);
        if (usr.isPresent() && usr.get().getID() != id) throw new IllegalArgumentException("Username already taken");

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "UPDATE USERDB SET username = ?, pass = ?, mail = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, eMail);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
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
        Optional<User> usr2 = findUser(username);
        if (usr2.isPresent()) return usr2.get().getWithoutPassword();
        else throw new RuntimeException("Something went wrong when updating the user");
    }

    /**
     * This method allows the user to change their password or e-mail.
     * The user will not be able to update their username through this method.
     *
     * @author Aldin Dervisi
     * @author Phillip-André Suhr
     * @implNote This method will not change the username. Use {@code updateUser(int id, String username, String password, String eMail)} for that instead.
     * @since 2021-02-23
     */
    @Override
    public User updateUser(String username, String password, String eMail) {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("Username must not be null");
        }

        createTable();

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            String sql = "UPDATE USERDB SET pass = ?, mail = ? WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, password);
            pstmt.setString(2, eMail);
            pstmt.setString(3, username);
            pstmt.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
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
        if (user.isPresent()) return user.get().getWithoutPassword();
        else throw new RuntimeException("Something went wrong when updating the user");
    }

    /**
     * This method creates the table containing the user information
     * <p>
     * IMPORTANT: This method is only needed for H2 as this database
     * gets generated dynamically on ServerApp start. Other databases
     * might not need it!
     */
    private void createTable() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(true);

            //@formatter:off
            String sql = "CREATE TABLE IF NOT EXISTS USERDB (" +
                    "id int NOT NULL AUTO_INCREMENT, " +
                    "username VARCHAR(255), " +
                    "mail VARCHAR(255), " +
                    "pass VARCHAR(255), " +
                    "PRIMARY KEY (username)," +
                    "UNIQUE (id))";
            //@formatter:on

            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
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
}
