package de.uol.swp.server.game.event;

import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.server.lobby.Lobby;
import de.uol.swp.server.message.AbstractServerInternalMessage;

/**
 * ServerInternalMessage sent by the LobbyService to the GameService to
 * initialise the game with the lobby's settings.
 *
 * @author Finn Haase
 * @author Phillip-André Suhr
 * @see de.uol.swp.server.lobby.LobbyService
 * @see de.uol.swp.server.game.GameService
 * @since 2021-03-18
 */
public class CreateGameInternalRequest extends AbstractServerInternalMessage {

    private final Lobby lobby;
    private final UserOrDummy first;

    /**
     * Constructor
     *
     * @param lobby The Lobby in which a game should be started
     * @param first The User who started the game
     */
    public CreateGameInternalRequest(Lobby lobby, UserOrDummy first) {
        super();
        this.lobby = lobby;
        this.first = first;
    }

    /**
     * Gets the first player in the lobby.
     *
     * @return The first player in the game
     */
    public UserOrDummy getFirst() {
        return first;
    }

    /**
     * Gets the lobby in which a game should be started
     *
     * @return The lobby
     */
    public Lobby getLobby() {
        return lobby;
    }
}
