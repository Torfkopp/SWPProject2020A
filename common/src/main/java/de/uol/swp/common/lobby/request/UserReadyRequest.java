package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;

/**
 * Request sent to the server when a User changes their Ready status.
 * <p>
 * This request is used both when a user changes to Ready as well as when a
 * user changes to not Ready.
 *
 * @author Eric Vuong
 * @author Maximilian Lindner
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @see de.uol.swp.common.lobby.message.UserReadyMessage
 * @since 2021-01-19
 */
public class UserReadyRequest extends AbstractLobbyRequest {

    private final boolean isReady;

    /**
     * Constructor
     *
     * @param lobbyName The Name of the lobby
     * @param user      The User who changed their ready status
     * @param isReady   true if the User is now ready, false if the user is now not ready (anymore)
     */
    public UserReadyRequest(LobbyName lobbyName, User user, boolean isReady) {
        super(lobbyName, user);
        this.isReady = isReady;
    }

    /**
     * If a player in a lobby is ready this method returns true, if not it returns false
     *
     * @return true if User is ready, false if not
     */
    public boolean isReady() {
        return this.isReady;
    }
}
