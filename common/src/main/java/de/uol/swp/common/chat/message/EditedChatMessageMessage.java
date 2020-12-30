package de.uol.swp.common.chat.message;

import de.uol.swp.common.chat.ChatMessage;

/**
 * Message sent by the server when a ChatMessage got edited successfully
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.chat.message.AbstractChatMessageMessage
 * @since 2020-12-17
 */
public class EditedChatMessageMessage extends AbstractChatMessageMessage {
    private final ChatMessage chatMsg;

    /**
     * Constructor
     *
     * @param chatMsg The edited ChatMessage
     * @since 2020-12-19
     */
    public EditedChatMessageMessage(ChatMessage chatMsg) {
        super(false);
        this.chatMsg = chatMsg;
    }

    public EditedChatMessageMessage(ChatMessage chatMsg, boolean isLobbyChatMessage, String lobbyName) {
        super(isLobbyChatMessage, lobbyName);
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
