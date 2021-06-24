package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;

/**
 * Request sent to the server when a user wants to join a random lobby
 *
 * @author Finn Haase
 * @author Sven Ahrens
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-04-08
 */
public class JoinRandomLobbyRequest extends AbstractLobbyRequest {

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     * @param user      User who wants to join a random lobby
     */
    public JoinRandomLobbyRequest(LobbyName lobbyName, Actor user) {
        super(lobbyName, user);
    }
}
