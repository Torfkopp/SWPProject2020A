package de.uol.swp.server.message;

import de.uol.swp.common.sessions.Session;
import de.uol.swp.common.user.User;

/**
 * This request is sent to the SessionService to get the new User
 * of the current Session to replace the old User
 *
 * @author Steven Luong
 * @see de.uol.swp.server.message.AbstractServerInternalMessage
 * @see de.uol.swp.server.sessionmanagement.SessionService
 * @since 2021-07-05
 */
public class ReplaceUserSessionInternalRequest extends AbstractServerInternalMessage {

    private final User newUser;
    private final Session session;

    /**
     * Constructor.
     *
     * @param newUser The new User who replaces the old User
     * @param session The current Session where the new User replaces the old User
     */
    public ReplaceUserSessionInternalRequest(User newUser, Session session) {
        this.newUser = newUser;
        this.session = session;
    }

    /**
     * Gets the current Session
     *
     * @return The current Session
     */
    public Session getCurrentSession() {
        return session;
    }

    /**
     * Gets the new User to replace the old User
     *
     * @return The new User
     */
    public User getNewUser() {
        return newUser;
    }
}
