package de.uol.swp.common.game.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Request sent to the server when a user wants to change the
 * pause status of the game
 *
 * @author Maximilian Lindner
 * @since 2021-05-21
 */
public class PauseGameRequest extends AbstractGameRequest {

    private final UserOrDummy userOrDummy;

    /**
     * Constructor
     *
     * @param originLobby The name of the lobby where the user wants to change the pause status of the game
     * @param userOrDummy The User who wants the change the pause status of the game
     */
    public PauseGameRequest(LobbyName originLobby, UserOrDummy userOrDummy) {
        super(originLobby);
        this.userOrDummy = userOrDummy;
    }

    /**
     * Gets the User
     *
     * @return The User who wants the change the pause status of the game
     */
    public UserOrDummy getUserOrDummy() {
        return userOrDummy;
    }
}
