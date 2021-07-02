package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

/**
 * A message containing a user who already had a session upon login
 * <p>
 * This response is sent to the client whose LoginRequest was successful
 * but the server detected an old session belonging to the user.
 *
 * @author Marvin Drees
 * @author Eric Vuong
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2021-03-02
 */
public class AlreadyLoggedInResponse extends AbstractResponseMessage {

    private final User user;

    /**
     * Constructor
     *
     * @param user The already logged in user
     */
    public AlreadyLoggedInResponse(User user) {
        this.user = user;
    }

    /**
     * Getter
     *
     * @return The already logged in user
     */
    public User getLoggedInUser() {
        return user;
    }
}