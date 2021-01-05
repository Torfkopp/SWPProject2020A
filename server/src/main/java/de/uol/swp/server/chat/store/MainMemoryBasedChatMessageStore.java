package de.uol.swp.server.chat.store;

import com.google.common.base.Strings;
import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.chat.dto.ChatMessageDTO;
import de.uol.swp.common.user.User;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This is a ChatMessage store.
 * <p>
 * This ChatMessage store only resides in the RAM of your computer
 * and only for as long as the server is running. Therefore, the messages have to be
 * added every time the server is started.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.server.chat.store.ChatMessageStore
 * @see de.uol.swp.server.chat.store.AbstractChatMessageStore
 * @since 2020-12-16
 */
public class MainMemoryBasedChatMessageStore extends AbstractChatMessageStore {

    private static final int MAX_HISTORY = 10000;
    // LinkedHashMaps keep insertion order and remove the eldest element when the override below returns true
    private final Map<Integer, ChatMessage> chatHistory = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, ChatMessage> eldest) {
            return size() > MAX_HISTORY;
        }
    };
    private int id_count;

    @Override
    public Optional<ChatMessage> findMessage(int id) {
        ChatMessage chatMessage = chatHistory.get(id);
        if (chatMessage != null) {
            return Optional.of(chatMessage);
        }
        return Optional.empty();
    }

    @Override
    public List<ChatMessage> getLatestMessages(int amount) {
        List<Map.Entry<Integer, ChatMessage>> list = new LinkedList<>(chatHistory.entrySet());
        Collections.reverse(list); // put the most recent messages to the top for proper [amount] limit
        List<ChatMessage> returnList = list.stream().limit(amount).map(Map.Entry::getValue).collect(Collectors.toList());
        Collections.reverse(returnList); // re-order the messages oldest to newest (newest at the bottom)
        return returnList;
    }

    @Override
    public ChatMessage createChatMessage(User author, String content) {
        if (author == null) {
            throw new IllegalArgumentException("Message author must not be null");
        }
        id_count += 1;
        ChatMessage chatMessage = new ChatMessageDTO(id_count, author, content);
        chatHistory.put(id_count, chatMessage);
        return chatMessage;
    }

    @Override
    public ChatMessage updateChatMessage(int id, String updatedContent) {
        ChatMessage messageToEdit = chatHistory.get(id);
        if (!Strings.isNullOrEmpty(updatedContent)) {
            messageToEdit.setContent(updatedContent);
        }
        return messageToEdit;
    }

    @Override
    public void removeChatMessage(int id) {
        chatHistory.remove(id);
    }
}
