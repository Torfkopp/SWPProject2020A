package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

/**
 * A message containing a session (typically for a new logged in user)
 * <p>
 * This response is sent to the client whose LoginRequest was successful
 *
 * @author Marvin Drees
 * @author Eric Vuong
 * @see AbstractResponseMessage
 * @since 2021-03-02
 */
public class AlreadyLoggedInResponse extends AbstractResponseMessage {

    private final User user;

    /**
     * Constructor
     *
     * @param user The already logged in session
     */
    public AlreadyLoggedInResponse(User user) {
        this.user = user;
    }

    public User getLoggedInUser() {
        return user;
    }
}
