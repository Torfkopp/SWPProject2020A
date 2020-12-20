package de.uol.swp.server.chat;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.server.chat.store.ChatMessageStore;

import java.util.List;
import java.util.Optional;

/**
 * The type Chat management.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see AbstractChatManagement
 * @see IChatManagement
 * @see ChatMessageStore
 * @see User
 * @since 2020-12-16
 */
public class ChatManagement extends AbstractChatManagement {

    private final ChatMessageStore chatMessageStore;

    /**
     * Constructor
     *
     * @param chatMessageStore object of the ChatMessageStore to be used
     * @see ChatMessageStore
     * @since 2020-12-16
     */
    @Inject
    public ChatManagement(ChatMessageStore chatMessageStore) {
        this.chatMessageStore = chatMessageStore;
    }

    @Override
    public List<ChatMessage> getLatestMessages(int amount) {
        return chatMessageStore.getLatestMessages(amount);
    }

    @Override
    public ChatMessage createChatMessage(User author, String content) {
        if (Strings.isNullOrEmpty(content)) {
            throw new ChatManagementException("Content must not be empty");
        } else if (author == null) {
            throw new ChatManagementException("Author must not be null");
        }
        return chatMessageStore.createChatMessage(author, content);
    }

    @Override
    public ChatMessage updateChatMessage(int id, String updatedContent) {
        Optional<ChatMessage> chatMessage = chatMessageStore.findMessage(id);
        if (chatMessage.isEmpty()) {
            throw new ChatManagementException("ChatMessage ID unknown");
        }
        return chatMessageStore.updateChatMessage(id, updatedContent);
    }

    @Override
    public void dropChatMessage(int id) {
        Optional<ChatMessage> chatMessage = chatMessageStore.findMessage(id);
        if (chatMessage.isEmpty()) {
            throw new ChatManagementException("ChatMessage ID unknown");
        }
        chatMessageStore.removeChatMessage(id);
    }
}
