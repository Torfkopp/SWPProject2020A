package de.uol.swp.common.chat.message;

import de.uol.swp.common.chat.ChatMessage;

/**
 * Message sent to clients when a new ChatMessage was saved on the server
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.chat.message.AbstractChatMessageMessage
 * @see de.uol.swp.common.chat.ChatMessage
 * @since 2020-12-17
 */
public class CreatedChatMessageMessage extends AbstractChatMessageMessage {
    private final ChatMessage msg;

    /**
     * Constructor
     * <p>
     * This constructor is used for CreatedChatMessageMessages sent to the global chat.
     * It sets the inherited isLobbyChatMessage to false and sets lobbyName to null.
     *
     * @param msg The ChatMessage that was created
     */
    public CreatedChatMessageMessage(ChatMessage msg) {
        super(null);
        this.msg = msg;
    }

    /**
     * Constructor
     * <p>
     * This constructor is used for CreatedChatMessageMessages sent to a lobby chat.
     * It sets the inherited isLobbyChatMessage and lobbyName attributes to the
     * parameters provided upon calling the constructor.
     *
     * @param msg       The ChatMessage that was created
     * @param lobbyName The Lobby this CreatedChatMessageMessage is being sent to
     * @since 2020-12-30
     */
    public CreatedChatMessageMessage(ChatMessage msg, String lobbyName) {
        super(lobbyName);
        this.msg = msg;
    }

    /**
     * Gets the created ChatMessage attribute
     *
     * @return The created ChatMessage
     */
    public ChatMessage getMsg() {
        return msg;
    }
}
