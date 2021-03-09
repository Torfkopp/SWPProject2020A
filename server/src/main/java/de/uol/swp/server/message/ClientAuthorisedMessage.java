package de.uol.swp.server.message;

import de.uol.swp.common.user.User;

/**
 * This message is used if a successful login occurred
 * <p>
 * This message is used to signalise all services it is relevant to
 * that someone just logged in successfully. In addition it carries
 * the information on whether or not this user is already authorized
 * somewhere else.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.server.message.AbstractServerInternalMessage
 * @see de.uol.swp.server.usermanagement.AuthenticationService
 * @since 2019-08-07
 */
public class ClientAuthorisedMessage extends AbstractServerInternalMessage {

    private final User user;
    private final boolean oldSession;

    /**
     * Constructor
     *
     * @param user       User whose client authorised successfully
     * @param oldSession Boolean whether an old session exists
     *
     * @see de.uol.swp.common.user.User
     * @since 2021-03-03
     */
    public ClientAuthorisedMessage(User user, boolean oldSession) {
        this.user = user;
        this.oldSession = oldSession;
    }

    /**
     * Gets the user attribute
     *
     * @return The user whose client authorised successfully
     *
     * @see de.uol.swp.common.user.User
     * @since 2019-08-07
     */
    public User getUser() {
        return user;
    }

    /**
     * Gets the oldSession attribute
     *
     * @return Whether or not a user has an old session
     *
     * @author Eric Vuong
     * @author Marvin Drees
     * @since 2021-03-02
     */
    public boolean hasOldSession() {
        return oldSession;
    }
}
