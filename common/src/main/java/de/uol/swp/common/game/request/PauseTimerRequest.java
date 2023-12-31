package de.uol.swp.common.game.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;

/**
 * Request sent to stop the roundTimer for the game.
 *
 * @author Alwin Bossert
 * @since 2021-05-02
 */
public class PauseTimerRequest extends AbstractGameRequest {

    private final Actor user;

    /**
     * Constructor
     *
     * @param lobbyName The lobby this game is taking place in
     * @param user      The user who started a trade/window
     */
    public PauseTimerRequest(LobbyName lobbyName, Actor user) {
        super(lobbyName);
        this.user = user;
    }

    /**
     * Gets the user who started a trade or openend a window,
     * where the timer should be stopped.
     *
     * @return The user
     */
    public Actor getActor() {
        return user;
    }
}
