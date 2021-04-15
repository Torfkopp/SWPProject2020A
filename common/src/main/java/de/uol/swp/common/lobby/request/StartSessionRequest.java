package de.uol.swp.common.lobby.request;

import de.uol.swp.common.LobbyName;
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

    /**
     * Constructor
     *
     * @param name The Name of the lobby
     * @param user The User who wants to start a game session
     */
    public StartSessionRequest(LobbyName name, User user) {
        super(name, user);
    }
}
