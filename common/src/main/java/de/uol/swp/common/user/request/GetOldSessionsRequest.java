package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

/**
 * Request sent from the client to be re-joined to all the
 * lobbys the user was in before.
 *
 * @author Maximilian Lindner
 * @author Marvin Drees
 * @see de.uol.swp.common.message.AbstractRequestMessagest
 * @since 2020-04-09
 */
public class GetOldSessionsRequest extends AbstractRequestMessage {

    private final User user;

    /**
     * Constructor
     *
     * @param user The user who wants to be re-joined into the previous lobbys
     */
    public GetOldSessionsRequest(User user) {
        this.user = user;
    }

    /**
     * Gets the User who wants to be re-joined into the previous lobbys
     *
     * @return The User who wants to be re-joined
     */
    public User getUser() {
        return user;
    }
}
