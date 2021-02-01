package de.uol.swp.common.user.message;

import de.uol.swp.common.message.AbstractServerMessage;

import java.util.Objects;

/**
 * A message to indicate a newly logged in user
 * <p>
 * This message is used to automatically update the user lists of every connected
 * client as soon as a user logs in successfully.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.message.AbstractServerMessage
 * @since 2017-03-17
 */
public class UserLoggedInMessage extends AbstractServerMessage {

    private static final long serialVersionUID = -2071886836547126480L;
    private String username;

    /**
     * Default constructor
     *
     * @implNote Do not use for valid login, since no username gets set
     * @implNote This constructor is needed for serialisation
     * @since 2017-03-17
     */
    public UserLoggedInMessage() {
    }

    /**
     * Constructor
     *
     * @param username The username of the newly logged in user
     *
     * @since 2017-03-17
     */
    public UserLoggedInMessage(String username) {
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
        UserLoggedInMessage that = (UserLoggedInMessage) o;
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
