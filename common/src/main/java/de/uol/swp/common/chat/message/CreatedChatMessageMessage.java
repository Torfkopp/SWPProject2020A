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
     *
     * @param msg The ChatMessage that was created
     * @since 2020-12-17
     */
    public CreatedChatMessageMessage(ChatMessage msg) {
        super(false);
        this.msg = msg;
    }

    public CreatedChatMessageMessage(ChatMessage msg, boolean isLobbyChatMessage, String lobbyName) {
        super(isLobbyChatMessage, lobbyName);
        this.msg = msg;
    }

    /**
     * Getter for the created ChatMessage attribute
     *
     * @return The created ChatMessage
     * @since 2020-12-17
     */
    public ChatMessage getMsg() {
        return msg;
    }
}
