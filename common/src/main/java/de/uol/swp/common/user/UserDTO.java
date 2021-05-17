package de.uol.swp.common.user;

import java.util.Objects;

/**
 * Objects of this class are used to transfer user data between the server and the
 * clients.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.user.request.RegisterUserRequest
 * @see de.uol.swp.common.user.response.AllOnlineUsersResponse
 * @since 2019-08-13
 */
public class UserDTO implements User {

    private final int id;
    private final String username;
    private final String password;
    private final String eMail;
    private final int usernameLength;

    /**
     * Constructor
     *
     * @param id       The user's ID
     * @param username The user's username
     * @param password The user's password
     * @param eMail    The user's e-mail address
     */
    public UserDTO(int id, String username, String password, String eMail) {
        if (!Objects.nonNull(username)) throw new IllegalArgumentException("Username must not be null");
        if (!Objects.nonNull(password)) throw new IllegalArgumentException("Password must not be null");
        if (!(username.length() < 21)) throw new IllegalArgumentException("Username can only be 20 symbols long");
        this.id = id;
        this.username = username;
        this.password = password;
        this.eMail = eMail;
        this.usernameLength = username.length();
    }

    /**
     * Copy constructor
     *
     * @param user User object to copy the values of
     *
     * @return User copy of the User object
     */
    public static User create(User user) {
        return new UserDTO(user.getID(), user.getUsername(), user.getPassword(), user.getEMail());
    }

    /**
     * Copy constructor leaving the password variable empty
     * <p>
     * This constructor is used for the user list because it would be a major security
     * flaw to send all user data including passwords to everyone connected.
     *
     * @param user User object to copy the values of
     *
     * @return UserDTO Copy of User object with the password variable left empty
     */
    public static User createWithoutPassword(User user) {
        return new UserDTO(user.getID(), user.getUsername(), "", user.getEMail());
    }

    @Override
    public String getEMail() {
        return eMail;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public User getWithoutPassword() {
        return new UserDTO(id, username, "", eMail);
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public int compareTo(UserOrDummy o) {
        Integer id_obj = id; // compareTo is only defined on the wrapper class, so we make one here
        if (o instanceof User) return id_obj.compareTo(o.getID());
        else return -1;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UserOrDummy) return compareTo((UserOrDummy) o) == 0;
        return false;
    }

    @Override
    public String toString() {
        return getUsername();
    }

    public int getUsernameLength() {return usernameLength;}
}
