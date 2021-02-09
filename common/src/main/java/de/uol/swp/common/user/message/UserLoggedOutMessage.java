package de.uol.swp.common.user.message;

import de.uol.swp.common.message.AbstractServerMessage;

import java.util.Objects;

/**
 * A message to indicate a user is logged out
 * <p>
 * This message is used to automatically update the user lists of every connected
 * client as soon as a user logs out successfully.
 *
 * @author Marco Grawunder
 * @since 2017-03-17
 */
public class UserLoggedOutMessage extends AbstractServerMessage {

    private static final long serialVersionUID = -2071886836547126480L;
    private final String username;

    /**
     * Constructor
     *
     * @param username The username of the newly logged out user
     *
     * @since 2017-03-17
     */
    public UserLoggedOutMessage(String username) {
        this.username = username;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLoggedOutMessage that = (UserLoggedOutMessage) o;
        return Objects.equals(username, that.username);
    }

    /**
     * Gets the username
     *
     * @return String containing the username
     *
     * @since 2017-03-17
     */
    public String getUsername() {
        return username;
    }
}
