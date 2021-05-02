package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserOrDummy;

/**
 * Request sent to unpause the roundTimer for the game.
 *
 * @author Alwin Bossert
 * @since 2021-05-02
 */
public class UnpauseTimerRequest extends AbstractGameRequest {

    private UserOrDummy user;

    /**
     * Constructor
     *
     * @param lobbyName The lobby this game is taking place in
     * @param user      The user who canceled a trade/window
     */
    public UnpauseTimerRequest(String lobbyName, UserOrDummy user) {
        super(lobbyName);
        this.user = user;
    }

    /**
     * Gets the user who canceled a trade or closed a window,
     * where the timer should be stopped.
     *
     * @return The user
     */
    public UserOrDummy getUser() {
        return user;
    }
}

