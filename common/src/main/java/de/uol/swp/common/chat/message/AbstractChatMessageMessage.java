package de.uol.swp.common.chat.message;

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
    private final String lobbyName;

    /**
     * Constructor
     * <p>
     * This constructor automatically sets the lobbyName attribute to null because none
     * was provided. This is independent of whether or not isLobbyChatMessage is actually
     * false.
     *
     * @param isLobbyChatMessage True, if the ChatMessage message is meant for a lobby chat &
     *                           False, if the ChatMessage message is meant for the global chat.
     */
    public AbstractChatMessageMessage(boolean isLobbyChatMessage) {
        this.isLobbyChatMessage = isLobbyChatMessage;
        this.lobbyName = null;
    }

    /**
     * Constructor
     * <p>
     * This constructor sets the ChatMessage message's isLobbyChatMessage and lobbyName
     * attributes to the parameters provided upon calling the constructor.
     *
     * @param isLobbyChatMessage the is lobby chat message
     * @param lobbyName          the lobby name
     */
    public AbstractChatMessageMessage(boolean isLobbyChatMessage, String lobbyName) {
        this.isLobbyChatMessage = isLobbyChatMessage;
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

    /**
     * Check if the ChatMessage message is destined for a lobby chat
     *
     * @return True, if the ChatMessage message is meant for a lobby chat; False if not
     */
    public boolean isLobbyChatMessage() {
        return isLobbyChatMessage;
    }
}
