package de.uol.swp.server.game.event;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.UserOrDummy;
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
    private final int moveTime;

    /**
     * Constructor
     *
     * @param lobby    The Lobby in which a game should be started
     * @param first    The User who started the game
     * @param moveTime The moveTime for the Game
     */
    public CreateGameInternalRequest(Lobby lobby, UserOrDummy first, int moveTime) {
        super();
        this.lobby = lobby;
        this.first = first;
        this.moveTime = moveTime;
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

    /**
     * Gets the moveTime for the game
     *
     * @return moveTime
     *
     * @author Alwin Bossert
     * @since 2021-05-02
     */
    public int getMoveTime() {
        return moveTime;
    }
}
