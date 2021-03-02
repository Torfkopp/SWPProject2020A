package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

import java.util.Objects;

/**
 * A message containing a session (typically for a new logged in user)
 * <p>
 * This response is sent to the client whose LoginRequest was successful
 *
 * @author Marvin Drees
 * @author Eric Vuong
 * @see de.uol.swp.common.user.request.LoginRequest
 * @see User
 * @see AbstractResponseMessage
 * @since 2021-03-02
 */
public class AlreadyLoggedInResponse extends AbstractResponseMessage {

    private final User user;

    /**
     * Constructor
     *
     * @param user The successfully logged in user
     */
    public AlreadyLoggedInResponse(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlreadyLoggedInResponse that = (AlreadyLoggedInResponse) o;
        return Objects.equals(user, that.user);
    }

    /**
     * Gets the user variable
     *
     * @return User object of the successfully logged in user
     */
    public User getUser() {
        return user;
    }
}
