package de.uol.swp.server.chat;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;
import de.uol.swp.server.chat.store.IChatMessageStore;

import java.util.List;
import java.util.Optional;

/**
 * Handles most chat related issues, e.g. creating/removing ChatMessages
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.server.chat.IChatManagement
 * @see de.uol.swp.server.chat.store.IChatMessageStore
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.chat.ChatMessage
 * @since 2020-12-16
 */
public class ChatManagement implements IChatManagement {

    private final IChatMessageStore chatMessageStore;

    /**
     * Constructor
     *
     * @param chatMessageStore Object of the ChatMessageStore to be used
     *
     * @see de.uol.swp.server.chat.store.IChatMessageStore
     * @since 2020-12-16
     */
    @Inject
    public ChatManagement(IChatMessageStore chatMessageStore) {
        this.chatMessageStore = chatMessageStore;
    }

    @Override
    public ChatMessage createChatMessage(Actor author, String content) throws ChatManagementException {
        if (Strings.isNullOrEmpty(content)) {
            throw new ChatManagementException("Content must not be empty");
        } else if (author == null) {
            throw new ChatManagementException("Author must not be null");
        }
        return chatMessageStore.createChatMessage(author, content);
    }

    @Override
    public ChatMessage createChatMessage(Actor author, String content,
                                         LobbyName originLobby) throws ChatManagementException {
        if (Strings.isNullOrEmpty(content)) {
            throw new ChatManagementException("Content must not be empty");
        } else if (author == null) {
            throw new ChatManagementException("Author must not be null");
        }
        return chatMessageStore.createChatMessage(author, content, originLobby);
    }

    @Override
    public void dropChatMessage(int id) throws ChatManagementException {
        Optional<ChatMessage> chatMessage = chatMessageStore.findMessage(id);
        if (chatMessage.isEmpty()) {
            throw new ChatManagementException("ChatMessage ID unknown");
        }
        chatMessageStore.removeChatMessage(id);
    }

    @Override
    public void dropChatMessage(int id, LobbyName originLobby) throws ChatManagementException {
        Optional<ChatMessage> chatMessage = chatMessageStore.findMessage(id, originLobby);
        if (chatMessage.isEmpty()) {
            throw new ChatManagementException("ChatMessage ID unknown");
        }
        chatMessageStore.removeChatMessage(id, originLobby);
    }

    @Override
    public void dropLobbyHistory(LobbyName originLobby) {
        chatMessageStore.removeLobbyHistory(originLobby);
    }

    @Override
    public Optional<ChatMessage> findChatMessage(int id) {
        return chatMessageStore.findMessage(id);
    }

    @Override
    public Optional<ChatMessage> findChatMessage(int id, LobbyName originLobby) {
        return chatMessageStore.findMessage(id, originLobby);
    }

    @Override
    public List<ChatMessage> getLatestMessages(int amount, LobbyName originLobby) {
        return chatMessageStore.getLatestMessages(amount, originLobby);
    }

    @Override
    public List<ChatMessage> getLatestMessages(int amount) {
        return chatMessageStore.getLatestMessages(amount);
    }

    @Override
    public ChatMessage updateChatMessage(int id, String updatedContent,
                                         LobbyName originLobby) throws ChatManagementException {
        Optional<ChatMessage> chatMessage = chatMessageStore.findMessage(id, originLobby);
        if (chatMessage.isEmpty()) {
            throw new ChatManagementException("ChatMessage ID unknown");
        }
        return chatMessageStore.updateChatMessage(id, updatedContent, originLobby);
    }

    @Override
    public ChatMessage updateChatMessage(int id, String updatedContent) throws ChatManagementException {
        Optional<ChatMessage> chatMessage = chatMessageStore.findMessage(id);
        if (chatMessage.isEmpty()) {
            throw new ChatManagementException("ChatMessage ID unknown");
        }
        return chatMessageStore.updateChatMessage(id, updatedContent);
    }
}
