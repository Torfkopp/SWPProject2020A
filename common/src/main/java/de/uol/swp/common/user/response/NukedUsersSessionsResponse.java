package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

/**
 * A response from server to client to confirm successful session removal
 * <p>
 * This response gets sent to new client whose users old sessions have been
 * successfully removed from the session store to allow a new login.
 *
 * @author Eric Vuong
 * @author Marvin Drees
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2021-03-03
 */
public class NukedUsersSessionsResponse extends AbstractResponseMessage {

    private final User user;

    /**
     * Constructor
     *
     * @param user The user whose old sessions have been nuked
     */
    public NukedUsersSessionsResponse(User user) {
        this.user = user;
    }

    /**
     * Getter
     *
     * @return The stored user object
     */
    public User getUser() {
        return user;
    }
}
