package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

/**
 * Response message for the CheckUserInLobbyRequest
 * <p>
 * This response gets sent to the Client which wants to change
 * the account details. It contains the user and a boolean.
 * If the user is in a lobby, he won't be able to change his account details.
 *
 * @author Alwin Bossert
 * @author Finn Haase
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2021-04-09
 */
public class CheckUserInLobbyResponse extends AbstractResponseMessage {

    private final User user;
    private final Boolean isInLobby;

    /**
     * Constructor
     *
     * @param user The user to check
     * @param isInLobby Checks all lobbies
     *
     * @since 2021-04-09
     */
    public CheckUserInLobbyResponse(User user, Boolean isInLobby) {
        this.user = user;
        this.isInLobby = isInLobby;
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

    /**
     * Gets a boolean (True if the user is in a lobby)
     *
     * @return Boolean
     *
     * @since 2021-04-09
     */
    public Boolean getIsInLobby(){return isInLobby;}
}
