package de.uol.swp.common.chat.message;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.message.AbstractServerMessage;

/**
 * Base class of all ChatMessage messages.
 * <p>
 * This class abstracts away the isLobbyChatMessage and lobbyName attributes
 * needed for checking which chat the ChatMessage message should be sent to.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @since 2020-12-30
 */
public abstract class AbstractChatMessageMessage extends AbstractServerMessage {

    private final boolean isLobbyChatMessage;
    private final LobbyName lobbyName;

    /**
     * Constructor
     * <p>
     * This constructor sets the ChatMessage message's isLobbyChatMessage and lobbyName
     * attributes to the parameters provided upon calling the constructor.
     *
     * @param lobbyName the lobby name
     */
    public AbstractChatMessageMessage(LobbyName lobbyName) {
        this.lobbyName = lobbyName;
        this.isLobbyChatMessage = (lobbyName != null);
    }

    /**
     * Getter for the lobbyName attribute
     *
     * @return The lobbyName of the destination lobby
     */
    public LobbyName getLobbyName() {
        return lobbyName;
    }

    /**
     * Check if the ChatMessage message is destined for a lobby chat
     *
     * @return True, if the ChatMessage message is meant for a lobby chat; False if not
     */
    public boolean isLobbyChatMessage() {
        return isLobbyChatMessage;
    }
}
