package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

/**
 * A response that the password was successfully changed
 * <p>
 * This response is only sent to clients that previously sent a
 * successfully executed ChangePasswordRequest.
 * Otherwise, an ExceptionMessage would have been sent.
 *
 * @author Steven Luong
 * @author Eric Vuong
 * @since 2020-12-03
 */
public class ChangeAccountDetailsSuccessfulResponse extends AbstractResponseMessage {
    private final User user;

    /**
     * Constructor
     *
     * @param user The already logged in user
     */
    public ChangeAccountDetailsSuccessfulResponse(User user) {
        this.user = user;
    }

    /**
     * Getter
     *
     * @return The already logged in user
     */
    public User getUser() {
        return user;
    }
}

