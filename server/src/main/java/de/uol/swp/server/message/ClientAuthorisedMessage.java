package de.uol.swp.server.message;

import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;

/**
 * This message is used if a successful login occurred
 * <p>
 * This message is used to signalise all services it is relevant to
 * that someone just logged in successfully
 *
 * @author Marco Grawunder
 * @see de.uol.swp.server.message.AbstractServerInternalMessage
 * @see de.uol.swp.server.usermanagement.AuthenticationService
 * @since 2019-08-07
 */
public class ClientAuthorisedMessage extends AbstractServerInternalMessage {

    private final User user;
    private final Session oldSession;

    /**
     * Constructor
     *
     * @param user User whose client authorised successfully
     * @see de.uol.swp.common.user.User
     * @since 2019-08-07
     */
    public ClientAuthorisedMessage(User user, Session oldSession) {
        this.user = user;
        this.oldSession = oldSession;
    }

    /**
     * Gets the user attribute
     *
     * @return The user whose client authorised successfully
     * @see de.uol.swp.common.user.User
     * @since 2019-08-07
     */
    public User getUser() {
        return user;
    }

    /**
     * Gets the oldSession attribute
     *
     * @return The session whose user is already logged
     * @author Eric Vuong
     * @author Marvin Drees
     * @see de.uol.swp.common.user.Session
     * @since 2021-03-02
     */
    public Session getOldSession() {
        return oldSession;
    }
}
