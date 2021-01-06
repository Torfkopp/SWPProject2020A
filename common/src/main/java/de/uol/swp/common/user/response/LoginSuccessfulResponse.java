package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

import java.util.Objects;

/**
 * A message containing a session (typically for a new logged in user)
 * <p>
 * This response is sent to the client whose LoginRequest was successful
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.user.request.LoginRequest
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2019-08-07
 */
public class LoginSuccessfulResponse extends AbstractResponseMessage {

    private static final long serialVersionUID = -9107206137706636541L;

    private final User user;

    /**
     * Constructor
     *
     * @param user The successfully logged in user
     * @since 2019-08-07
     */
    public LoginSuccessfulResponse(User user) {
        this.user = user;
    }

    /**
     * Gets the user variable
     *
     * @return User object of the successfully logged in user
     * @since 2019-08-07
     */
    public User getUser() {
        return user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginSuccessfulResponse that = (LoginSuccessfulResponse) o;
        return Objects.equals(user, that.user);
    }
}
