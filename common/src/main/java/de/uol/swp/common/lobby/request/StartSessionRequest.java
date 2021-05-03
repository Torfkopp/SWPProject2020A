package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.User;

/**
 * Request is send when the owner of a lobby wants to start a game session.
 *
 * @author Eric Vuong
 * @author Maximilian Lindner
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @see de.uol.swp.common.lobby.message.StartSessionMessage
 * @since 2021-01-21
 */
public class StartSessionRequest extends AbstractLobbyRequest {

    private final int moveTime;

    /**
     * Constructor
     *
     * @param name The Name of the lobby
     * @param user The User who wants to start a game session
     */
    public StartSessionRequest(String name, User user, int moveTime) {
        super(name, user);
        this.moveTime = moveTime;
    }

    /**
     * Gets the moveTime for the game.
     *
     * @return moveTime
     *
     * @author Alwin Bossert
     * @since 2021-05-02
     */
    public int getMoveTime() {
        return moveTime;
    }
}
