package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;

/**
 * Request sent to the server when a user wants to leave a lobby
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @since 2019-10-08
 */
public class LeaveLobbyRequest extends AbstractLobbyRequest {

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     * @param user      User who wants to leave the lobby
     *
     * @since 2019-10-08
     */
    public LeaveLobbyRequest(LobbyName lobbyName, User user) {
        super(lobbyName, user);
    }
}
