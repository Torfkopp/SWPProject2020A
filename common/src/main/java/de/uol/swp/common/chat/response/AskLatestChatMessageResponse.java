package de.uol.swp.common.chat.response;

import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.chat.dto.ChatMessageDTO;
import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.LinkedList;
import java.util.List;

/**
 * Response message for the AskLatestChatMessageRequest
 * <p>
 * This message gets sent to the client that sent an AskLatestChatMessageRequest.
 * It contains a List with ChatMessage objects of the specified length as specified in the
 * AskLatestChatMessageRequest.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @see de.uol.swp.common.chat.ChatMessage
 * @see de.uol.swp.common.chat.request.AskLatestChatMessageRequest#getAmount()
 * @since 2020-12-17
 */
public class AskLatestChatMessageResponse extends AbstractResponseMessage {
    private final List<ChatMessage> chatHistory = new LinkedList<>();
    private final String lobbyName;

    /**
     * Constructor
     * <p>
     * This constructor is used for AskLatestChatMessageResponses that were
     * requested for the global chat. It sets the lobbyName attribute to null.
     *
     * @param latestMessages a List of ChatMessage object of size as requested or less
     */
    public AskLatestChatMessageResponse(List<ChatMessage> latestMessages) {
        for (ChatMessage msg : latestMessages) {
            chatHistory.add(ChatMessageDTO.create(msg));
        }
        this.lobbyName = null;
    }

    /**
     * Constructor
     * <p>
     * This constructor is used for AskLatestChatMessageResponses that were
     * requested for a lobby chat. It sets the lobbyName attribute to the
     * parameter provided upon calling the constructor.
     *
     * @param latestMessages a List of ChatMessage object of size as requested or less
     * @param lobbyName      The Lobby the AskLatestChatMessageResponse was requested for
     * @since 2020-12-30
     */
    public AskLatestChatMessageResponse(List<ChatMessage> latestMessages, String lobbyName) {
        for (ChatMessage msg : latestMessages) {
            chatHistory.add(ChatMessageDTO.create(msg));
        }
        this.lobbyName = lobbyName;
    }

    /**
     * Getter for the chatHistory attribute
     *
     * @return The List of ChatMessage object of size as requested or less
     */
    public List<ChatMessage> getChatHistory() {
        return chatHistory;
    }

    /**
     * Getter for the lobbyName attribute
     *
     * @return The name of the Lobby this AskLatestChatMessageResponse was requested for
     * @since 2021-01-02
     */
    public String getLobbyName() {
        return lobbyName;
    }
}
