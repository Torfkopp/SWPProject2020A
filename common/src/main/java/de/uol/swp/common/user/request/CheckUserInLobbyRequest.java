package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

/**
 * A request sent from client to server to check if the user is in a lobby
 *
 * @author Alwin Bossert
 * @author Finn Haase
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.message.AbstractRequestMessage
 * @since 2021-04-09
 */
public class CheckUserInLobbyRequest extends AbstractRequestMessage {

    private final User user;

    /**
     * Constructor
     *
     * @param user The user to check
     *
     * @since 2021-04-09
     */
    public CheckUserInLobbyRequest(User user) {
        this.user = user;
    }

    /**
     * Gets the user variable
     *
     * @return User object
     *
     * @since 2021-04-09
     */
    public User getUser() {
        return user;
    }
}
