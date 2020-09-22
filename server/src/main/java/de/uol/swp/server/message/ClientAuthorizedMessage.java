package de.uol.swp.server.message;

import de.uol.swp.common.user.User;

/**
 * This message is used if a successful login occurred
 *
 * This message is used to signalize all Services it is relevant to, that someone
 * just logged in successfully
 *
 * @see de.uol.swp.server.message.AbstractServerInternalMessage
 * @see de.uol.swp.server.usermanagement.AuthenticationService
 * @author Marco Grawunder
 * @since 2019-08-07
 */
public class ClientAuthorizedMessage extends AbstractServerInternalMessage {

    private final User user;

    /**
     * Constructor
     *
     * @param user user whose client authorized successfully
     * @see de.uol.swp.common.user.User
     * @since 2019-08-07
     */
    public ClientAuthorizedMessage(User user) {
        super();
        this.user = user;
    }

    /**
     * Getter for the user attribute
     *
     * @return the user whose client authorized successfully
     * @see de.uol.swp.common.user.User
     * @since 2019-08-07
     */
    public User getUser() {
        return user;
    }
}
