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

    private final String username;
    private final String password;
    private final String eMail;

    /**
     * Constructor
     *
     * @param username The user's username
     * @param password The user's password
     * @param eMail    The user's e-mail address
     *
     * @since 2019-08-13
     */
    public UserDTO(String username, String password, String eMail) {
        assert Objects.nonNull(username);
        assert Objects.nonNull(password);
        this.username = username;
        this.password = password;
        this.eMail = eMail;
    }

    /**
     * Copy constructor
     *
     * @param user User object to copy the values of
     *
     * @return User copy of the User object
     *
     * @since 2019-08-13
     */
    public static User create(User user) {
        return new UserDTO(user.getUsername(), user.getPassword(), user.getEMail());
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
     *
     * @since 2019-08-13
     */
    public static User createWithoutPassword(User user) {
        return new UserDTO(user.getUsername(), "", user.getEMail());
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
    public String getUsername() {
        return username;
    }

    @Override
    public User getWithoutPassword() {
        return new UserDTO(username, "", eMail);
    }

    @Override
    public int compareTo(User o) {
        return username.compareTo(o.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserDTO)) {
            return false;
        }
        return Objects.equals(this.username, ((UserDTO) obj).username);
    }
}
