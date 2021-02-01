package de.uol.swp.common.chat.message;

import de.uol.swp.common.chat.ChatMessage;

/**
 * Message sent by the server when a ChatMessage got edited successfully
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.chat.message.AbstractChatMessageMessage
 * @see de.uol.swp.common.chat.ChatMessage
 * @since 2020-12-17
 */
public class EditedChatMessageMessage extends AbstractChatMessageMessage {

    private final ChatMessage chatMsg;

    /**
     * Constructor
     * <p>
     * This constructor is used for EditedChatMessageMessages sent to the global chat.
     * It sets the inherited isLobbyChatMessage to false and sets lobbyName to null.
     *
     * @param chatMsg The edited ChatMessage
     */
    public EditedChatMessageMessage(ChatMessage chatMsg) {
        super(null);
        this.chatMsg = chatMsg;
    }

    /**
     * Constructor
     * <p>
     * This constructor is used for EditedChatMessageMessages sent to a lobby chat.
     * It sets the inherited isLobbyChatMessage and lobbyName attributes to the
     * parameters provided upon calling the constructor.
     *
     * @param chatMsg   The edited ChatMessage
     * @param lobbyName The Lobby this EditedChatMessageMessage is being sent to
     *
     * @since 2020-12-30
     */
    public EditedChatMessageMessage(ChatMessage chatMsg, String lobbyName) {
        super(lobbyName);
        this.chatMsg = chatMsg;
    }

    /**
     * Gets the chatMsg attribute
     *
     * @return The edited ChatMessage
     */
    public ChatMessage getMsg() {
        return chatMsg;
    }
}
