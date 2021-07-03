package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

/**
 * A request sent from client to server when a user wants to log his old session out
 * <p>
 * This message is used to tell the server to nuke all existing sessions of a certain user.
 *
 * @author Eric Vuong
 * @author Marvin Drees
 * @see de.uol.swp.common.message.AbstractRequestMessage
 * @since 2021-03-03
 */
public class NukeUsersSessionsRequest extends AbstractRequestMessage {

    private final User user;

    /**
     * Constructor
     *
     * @since 2021-03-03
     */
    public NukeUsersSessionsRequest(User user) {
        super();
        this.user = user;
    }

    @Override
    public boolean authorisationNeeded() {
        return false;
    }

    /**
     * Getter
     *
     * @return User whose sessions shall be nuked
     */
    public User getUser() {
        return user;
    }
}