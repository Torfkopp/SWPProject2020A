package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;

import java.util.Objects;

/**
 * A request sent from client to server when a user
 * tries to log in with a username and a password
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.message.AbstractRequestMessage
 * @since 2017-03-17
 */
public class LoginRequest extends AbstractRequestMessage {

    private static final long serialVersionUID = 7793454958390539421L;
    private final String username;
    private final String password;

    /**
     * Constructor
     *
     * @param username The user's username
     * @param password The user's password
     *
     * @since 2017-03-17
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean authorisationNeeded() {
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginRequest that = (LoginRequest) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password);
    }

    /**
     * Gets the password variable
     *
     * @return String containing the user's password
     *
     * @since 2017-03-17
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the username variable
     *
     * @return String containing the user's username
     *
     * @since 2017-03-17
     */
    public String getUsername() {
        return username;
    }
}
