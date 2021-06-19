package de.uol.swp.common.game.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;

/**
 * Request sent to the server when a user wants to end his turn
 *
 * @author Alwin Bossert
 * @author Mario Fokken
 * @author Marvin Drees
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-01-15
 */
public class EndTurnRequest extends AbstractGameRequest {

    private final Actor user;

    /**
     * Constructor
     * <p>
     * This constructor is used to determine the user that
     * sent this request.
     *
     * @param user The user wanting to end the turn
     *
     * @since 2021-01-15
     */
    public EndTurnRequest(Actor user, LobbyName originLobby) {
        super(originLobby);
        this.user = user;
    }

    /**
     * Gets the user attribute
     *
     * @return The user of the EndTurnRequest
     */
    public Actor getUser() {
        return user;
    }
}
