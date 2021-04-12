package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserOrDummy;

/**
 * Request sent to the server when a user wants to check if the lobby
 * he joined is already in a game.
 *
 * @author Maximilian Lindner
 * @author Marvin Drees
 * @see de.uol.swp.common.game.request.AbstractGameRequest
 * @since 2021-04-09
 */
public class CheckForGameRequest extends AbstractGameRequest {

    private final UserOrDummy user;

    /**
     * Constructor
     *
     * @param originLobby The Lobby from which a request originated from
     */
    public CheckForGameRequest(String originLobby, UserOrDummy user) {
        super(originLobby);
        this.user = user;
    }

    /**
     * Gets the user who wants to check his lobby
     *
     * @return The User who joined a lobby
     */
    public UserOrDummy getUser() {
        return user;
    }
}
