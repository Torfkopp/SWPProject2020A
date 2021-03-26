package de.uol.swp.common.game.message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Message sent to create a new game
 *
 * @author Mario Fokken
 * @since 2021-01-24
 */
public class CreateGameMessage extends AbstractGameMessage {

    private final Lobby lobby;

    /**
     * Constructor
     *
     * @param lobby The lobby this game is taking place in
     * @param first The first player
     */
    public CreateGameMessage(Lobby lobby, UserOrDummy first) {
        super(lobby.getName(), first);
        this.lobby = lobby;
    }

    /**
     * Gets the first player
     *
     * @return The first player
     */
    public UserOrDummy getFirst() {
        return super.getUser();
    }

    /**
     * Gets the lobby the game is taking place in
     *
     * @return The Lobby the game is taking place in
     */
    public Lobby getLobby() {
        return lobby;
    }
}
