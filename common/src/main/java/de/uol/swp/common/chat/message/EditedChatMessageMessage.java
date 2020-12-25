package de.uol.swp.common.chat.message;

import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.message.AbstractServerMessage;

/**
 * Message sent by the server when a ChatMessage got edited successfully
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.message.AbstractServerMessage
 * @since 2020-12-17
 */
public class EditedChatMessageMessage extends AbstractServerMessage {
    private final ChatMessage chatMsg;

    /**
     * Constructor
     *
     * @param chatMsg The edited ChatMessage
     * @since 2020-12-19
     */
    public EditedChatMessageMessage(ChatMessage chatMsg) {
        this.chatMsg = chatMsg;
    }

    /**
     * Getter for the chatMsg attribute
     *
     * @return The edited ChatMessage
     * @since 2020-12-19
     */
    public ChatMessage getMsg() {
        return chatMsg;
    }
}
