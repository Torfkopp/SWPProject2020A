package de.uol.swp.common.game.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;

/**
 * Request sent to the server when a user wants to change his
 * autoRoll status.
 *
 * @author Maximilian Lindner
 * @see de.uol.swp.common.game.request.AbstractGameRequest
 * @since 2021 -02-21
 */
public class ChangeAutoRollStateRequest extends AbstractGameRequest {

    private final User user;
    private final boolean autoRoll;

    /**
     * Constructor
     *
     * @param originLobby The lobby where the User wants to change the autoRoll status
     * @param user        The User who wants to change the status
     * @param autoRoll    The new value of the autoRoll status
     */
    public ChangeAutoRollStateRequest(LobbyName originLobby, User user, boolean autoRoll) {
        super(originLobby);
        this.user = user;
        this.autoRoll = autoRoll;
    }

    /**
     * Gets user who wants to change the autoRoll status.
     *
     * @return The requesting User
     */
    public User getUser() {
        return user;
    }

    /**
     * The new autoRoll status
     *
     * @return The new status
     */
    public boolean isAutoRollEnabled() {
        return autoRoll;
    }
}
