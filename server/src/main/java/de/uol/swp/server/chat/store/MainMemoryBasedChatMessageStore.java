package de.uol.swp.server.chat.store;

import com.google.common.base.Strings;
import de.uol.swp.common.LobbyName;
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
    private static final int MAX_LOBBY_HISTORY = 1000;
    // LinkedHashMaps keep insertion order and remove the eldest element when the override below returns true
    private final Map<Integer, ChatMessage> chatHistory = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, ChatMessage> eldest) {
            return size() > MAX_HISTORY;
        }
    };
    private final Map<LobbyName, Map<Integer, ChatMessage>> lobbyChatHistories = new HashMap<>();
    private int id_count;

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
    public ChatMessage createChatMessage(User author, String content, LobbyName originLobby) {
        if (author == null) {
            throw new IllegalArgumentException("Message author must not be null");
        } else if (originLobby == null) {
            return createChatMessage(author, content);
        } else {
            ensureLobbyChatHistory(originLobby);
            id_count += 1;
            ChatMessage chatMessage = new ChatMessageDTO(id_count, author, content);
            lobbyChatHistories.get(originLobby).put(id_count, chatMessage);
            return chatMessage;
        }
    }

    @Override
    public Optional<ChatMessage> findMessage(int id) {
        ChatMessage chatMessage = chatHistory.get(id);
        if (chatMessage != null) {
            return Optional.of(chatMessage);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ChatMessage> findMessage(int id, LobbyName originLobby) {
        if (originLobby == null) {
            return findMessage(id);
        } else {
            ensureLobbyChatHistory(originLobby);
            ChatMessage chatMessage = lobbyChatHistories.get(originLobby).get(id);
            if (chatMessage != null) {
                return Optional.of(chatMessage);
            }
            return Optional.empty();
        }
    }

    @Override
    public List<ChatMessage> getLatestMessages(int amount) {
        List<Map.Entry<Integer, ChatMessage>> list = new LinkedList<>(chatHistory.entrySet());
        Collections.reverse(list); // put the most recent messages to the top for proper [amount] limit
        List<ChatMessage> returnList = list.stream().limit(amount).map(Map.Entry::getValue)
                                           .collect(Collectors.toList());
        Collections.reverse(returnList); // re-order the messages oldest to newest (newest at the bottom)
        return returnList;
    }

    @Override
    public List<ChatMessage> getLatestMessages(int amount, LobbyName originLobby) {
        if (originLobby == null) {
            return getLatestMessages(amount);
        } else {
            ensureLobbyChatHistory(originLobby);
            List<Map.Entry<Integer, ChatMessage>> list = new LinkedList<>(
                    lobbyChatHistories.get(originLobby).entrySet());
            Collections.reverse(list);
            List<ChatMessage> returnList = list.stream().limit(amount).map(Map.Entry::getValue)
                                               .collect(Collectors.toList());
            Collections.reverse(returnList);
            return returnList;
        }
    }

    @Override
    public void removeChatMessage(int id) {
        chatHistory.remove(id);
    }

    @Override
    public void removeChatMessage(int id, LobbyName originLobby) {
        if (originLobby == null) {
            removeChatMessage(id);
        } else {
            ensureLobbyChatHistory(originLobby);
            lobbyChatHistories.get(originLobby).remove(id);
        }
    }

    @Override
    public void removeLobbyHistory(LobbyName originLobby) {
        if (originLobby != null) {
            lobbyChatHistories.remove(originLobby);
        }
    }

    @Override
    public ChatMessage updateChatMessage(int id, String updatedContent) {
        ChatMessage messageToEdit = chatHistory.get(id);
        if (messageToEdit != null) {
            if (!Strings.isNullOrEmpty(updatedContent)) {
                messageToEdit.setContent(updatedContent);
            }
            return messageToEdit;
        } else {
            throw new IllegalArgumentException("ChatMessage ID unknown");
        }
    }

    @Override
    public ChatMessage updateChatMessage(int id, String updatedContent, LobbyName originLobby) {
        if (originLobby == null) {
            return updateChatMessage(id, updatedContent);
        } else {
            ensureLobbyChatHistory(originLobby);
            ChatMessage messageToEdit = lobbyChatHistories.get(originLobby).get(id);
            if (messageToEdit != null) {
                if (!Strings.isNullOrEmpty(updatedContent)) {
                    messageToEdit.setContent(updatedContent);
                }
                return messageToEdit;
            } else {
                throw new IllegalArgumentException("ChatMessage ID unknown");
            }
        }
    }

    /**
     * Helper method to ensure a Map for the originLobby exists before trying
     * to access it in order to avoid a NullPointerException.
     *
     * @param originLobby The Lobby for which to ensure a Map of ChatMessages exists
     *
     * @since 2021-01-02
     */
    private void ensureLobbyChatHistory(LobbyName originLobby) {
        if (lobbyChatHistories.get(originLobby) == null) {
            lobbyChatHistories.put(originLobby, new LinkedHashMap<>() {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Integer, ChatMessage> eldest) {
                    return size() > MAX_LOBBY_HISTORY;
                }
            });
        }
    }
}
