package de.uol.swp.common.game.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;

/**
 * Request sent to unpause the roundTimer for the game.
 *
 * @author Alwin Bossert
 * @since 2021-05-02
 */
public class UnpauseTimerRequest extends AbstractGameRequest {

    private final Actor user;

    /**
     * Constructor
     *
     * @param lobbyName The lobby this game is taking place in
     * @param user      The user who canceled a trade/window
     */
    public UnpauseTimerRequest(LobbyName lobbyName, Actor user) {
        super(lobbyName);
        this.user = user;
    }

    /**
     * Gets the user who canceled a trade or closed a window,
     * where the timer should be stopped.
     *
     * @return The user
     */
    public Actor getUser() {
        return user;
    }
}

