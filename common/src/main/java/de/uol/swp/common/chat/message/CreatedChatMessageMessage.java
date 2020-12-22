package de.uol.swp.common.chat.message;

import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.message.AbstractServerMessage;

/**
 * Message sent to clients when a new ChatMessage was saved on the server
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.message.AbstractServerMessage
 * @see de.uol.swp.common.chat.ChatMessage
 * @since 2020-12-17
 */
public class CreatedChatMessageMessage extends AbstractServerMessage {
    private final ChatMessage msg;

    /**
     * Constructor
     *
     * @param msg The ChatMessage that was created
     * @since 2020-12-17
     */
    public CreatedChatMessageMessage(ChatMessage msg) {
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
