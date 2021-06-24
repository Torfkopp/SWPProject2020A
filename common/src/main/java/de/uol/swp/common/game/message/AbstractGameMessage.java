package de.uol.swp.common.game.message;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.message.AbstractServerMessage;
import de.uol.swp.common.user.Actor;

/**
 * Base class of all game messages.
 * <p>
 * This class abstracts away the lobbyName attribute
 * needed for checking which lobby the game message should be sent to.
 *
 * @author Alwin Bossert
 * @author Mario Fokken
 * @author Marvin Drees
 * @see de.uol.swp.common.message.AbstractServerMessage
 * @since 2021-01-15
 */
public abstract class AbstractGameMessage extends AbstractServerMessage {

    private final LobbyName lobbyName;
    private final Actor user;

    /**
     * Constructor
     *
     * @param lobbyName The lobby name
     * @param user      The user
     */
    public AbstractGameMessage(LobbyName lobbyName, Actor user) {
        this.lobbyName = lobbyName;
        this.user = user;
    }

    /**
     * Gets the lobbyName attribute
     *
     * @return The lobbyName of the destination lobby
     */
    public LobbyName getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the user
     *
     * @return user The user
     */
    public Actor getActor() {
        return user;
    }
}
