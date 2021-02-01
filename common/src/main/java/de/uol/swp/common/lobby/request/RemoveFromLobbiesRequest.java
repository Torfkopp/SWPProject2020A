package de.uol.swp.common.lobby.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

public class RemoveFromLobbiesRequest extends AbstractRequestMessage {

    private final User user;

    /**
     * Constructor
     *
     * @param user User who wants to logout
     *
     * @author Finn Haase
     * @since 2021-01-28
     */
    public RemoveFromLobbiesRequest(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
