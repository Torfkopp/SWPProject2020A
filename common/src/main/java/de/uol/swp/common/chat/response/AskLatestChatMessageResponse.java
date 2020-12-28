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

    /**
     * Constructor
     *
     * @param latestMessages a List of ChatMessage object of size as requested or less
     * @since 2020-12-17
     */
    public AskLatestChatMessageResponse(List<ChatMessage> latestMessages) {
        for (ChatMessage msg : latestMessages) {
            chatHistory.add(ChatMessageDTO.create(msg));
        }
    }

    /**
     * Getter for the chatHistory attribute
     *
     * @return The List of ChatMessage object of size as requested or less
     * @since 2020-12-17
     */
    public List<ChatMessage> getChatHistory() {
        return chatHistory;
    }
}
