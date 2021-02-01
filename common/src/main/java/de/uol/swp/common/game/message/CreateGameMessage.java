package de.uol.swp.common.game.message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;

/**
 * Message sent to create a new game
 *
 * @author Mario Fokken
 * @since 2021-01-24
 */
public class CreateGameMessage extends AbstractGameMessage {

    private final Lobby lobby;
    private final User first;

    public CreateGameMessage(Lobby lobby, User first) {
        this.lobby = lobby;
        this.first = first;
    }

    public User getFirst() {
        return first;
    }

    public Lobby getLobby() {
        return lobby;
    }
}
