package de.uol.swp.common.game.message;

import de.uol.swp.common.message.AbstractServerMessage;

/**
 * Base class of all game messages.
 * <p>
 * This class abstracts away the lobbyName attribute
 * needed for checking which lobby the game message should be sent to.
 *
 * @author Alwin Bossert
 * @author Mario Fokken
 * @author Marvin Drees
 * @see AbstractServerMessage
 * @since 2021-01-15
 */
public abstract class AbstractGameMessage extends AbstractServerMessage {
    private String lobbyName;

    /**
     * Default constructor
     * <p>
     * This constructor is needed for serialisation
     */
    public AbstractGameMessage() {
    }

    /**
     * Constructor
     * <p>
     * This constructor sets the lobbyName attribute to the parameters
     * provided upon calling the constructor.
     *
     * @param lobbyName the lobby name
     */
    public AbstractGameMessage(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Getter for the lobbyName attribute
     *
     * @return The lobbyName of the destination lobby
     */
    public String getLobbyName() {
        return lobbyName;
    }
}
