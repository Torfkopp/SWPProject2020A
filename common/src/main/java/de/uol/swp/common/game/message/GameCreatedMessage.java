package de.uol.swp.common.game.message;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;

/**
 * Message sent when a new game was created
 *
 * @author Mario Fokken
 * @since 2021-01-24
 */
public class GameCreatedMessage extends AbstractGameMessage {

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby a game was started in
     * @param first     The first player
     */
    public GameCreatedMessage(LobbyName lobbyName, Actor first) {
        super(lobbyName, first);
    }

    /**
     * Gets the first player
     *
     * @return The first player
     */
    public Actor getFirst() {
        return super.getActor();
    }
}
