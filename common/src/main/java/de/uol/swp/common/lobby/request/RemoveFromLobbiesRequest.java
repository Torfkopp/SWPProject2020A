package de.uol.swp.common.lobby.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

/**
 * Request sent to the server when a user wants to leave all lobbies they are in
 *
 * @author Aldin Dervisi
 * @author Finn Haase
 * @see de.uol.swp.common.message.AbstractRequestMessage
 * @since 2021-01-28
 */
public class RemoveFromLobbiesRequest extends AbstractRequestMessage {

    private final User user;

    /**
     * Constructor
     *
     * @param user The user who wants to be removed from all lobbies
     */
    public RemoveFromLobbiesRequest(User user) {
        this.user = user;
    }

    /**
     * Gets the user who wants to be removed from all lobbies.
     *
     * @return The user who wants to be removed from all lobbies
     */
    public User getUser() {
        return user;
    }
}
