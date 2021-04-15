package de.uol.swp.common.game.request;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Request sent to return to the Pre-Game lobby state
 *
 * @author Steven Luong
 * @author Finn Haase
 * @since 2021-03-22
 */
public class ReturnToPreGameLobbyRequest extends AbstractRequestMessage {

    private final LobbyName lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName The lobby this game takes place in
     */
    public ReturnToPreGameLobbyRequest(LobbyName lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Gets the lobby name.
     *
     * @return The name of the lobby
     */
    public LobbyName getLobbyName() {
        return lobbyName;
    }
}
