package de.uol.swp.common.chat.response;

import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.chat.dto.ChatMessageDTO;
import de.uol.swp.common.chat.request.AskLatestChatMessageRequest;
import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.LinkedList;
import java.util.List;

/**
 * Response message for the AskLatestChatMessageRequest
 * <p>
 * This message gets sent to the client that sent an AskLatestChatMessageRequest.
 * It contains a list with ChatMessage objects of the specified length as specified in the
 * AskLatestChatMessageRequest.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see AbstractResponseMessage
 * @see ChatMessage
 * @see AskLatestChatMessageRequest#getAmount()
 * @since 2020-12-17
 */
public class AskLatestChatMessageResponse extends AbstractResponseMessage {
    private final List<ChatMessage> chatHistory = new LinkedList<>();

    /**
     * Constructor
     *
     * @param latestMessages A list of ChatMessage objects smaller or as big as requested
     * @since 2020-12-17
     */
    public AskLatestChatMessageResponse(List<ChatMessage> latestMessages) {
        for (ChatMessage msg : latestMessages) {
            chatHistory.add(ChatMessageDTO.create(msg));
        }
    }

    /**
     * Gets the chatHistory attribute
     *
     * @return A list of ChatMessage objects smaller or as big as requested
     * @since 2020-12-17
     */
    public List<ChatMessage> getChatHistory() {
        return chatHistory;
    }
}
