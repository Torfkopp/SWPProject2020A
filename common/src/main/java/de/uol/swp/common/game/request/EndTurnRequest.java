package de.uol.swp.common.game.request;

import de.uol.swp.common.user.User;

/**
 * Request sent to the server when a user wants to end his turn
 *
 * @author Alwin Bossert
 * @author Mario Fokken
 * @author Marvin Drees
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-1-15
 */
public class EndTurnRequest extends AbstractGameRequest {
    private final User user;

    /**
     * Constructor
     * <p>
     * This constructor is used to determine the user that
     * sent this request.
     *
     * @param user The user wanting to end the turn
     * @since 2021-1-15
     */
    public EndTurnRequest(User user, String originLobby) {
        super(originLobby);
        this.user = user;
    }

    /**
     * Gets the user attribute
     *
     * @return The user of the EndTurnRequest
     */
    public User getUser() {
        return user;
    }
}
