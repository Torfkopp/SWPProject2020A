package de.uol.swp.server.chat.store;

import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.user.User;

import java.util.List;
import java.util.Optional;

/**
 * Interface to unify different kinds of ChatMessageStores
 * enabling an easy exchange.
 *
 * @author Temmo Junkhoff
 * @see de.uol.swp.server.chat.store.AbstractChatMessageStore
 * @see de.uol.swp.server.chat.store.MainMemoryBasedChatMessageStore
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.chat.ChatMessage
 * @since 2020-12-16
 */
public interface ChatMessageStore {

    /**
     * Method to retrieve a ChatMessage from the store through its ID
     *
     * @param id The ID to look up
     * @return The ChatMessage object with that ID if found
     */
    Optional<ChatMessage> findMessage(int id);

    /**
     * Method to get the last [amount] of messages
     *
     * @param amount Amount of ChatMessages to be retrieved
     * @return List of latest ChatMessages, sorted newest to oldest
     */
    List<ChatMessage> getLatestMessages(int amount);

    /**
     * Method to create a ChatMessage
     *
     * @param author  The User who wrote the ChatMessage
     * @param content The content of the message
     * @return The created ChatMessage object
     * @implSpec ID and Timestamp need to be added on creation
     */
    ChatMessage createChatMessage(User author, String content);

    /**
     * Method to update contents of ChatMessage
     *
     * @param id             The ChatMessage ID to update
     * @param updatedContent The new content of the message
     * @return The ChatMessage
     */
    ChatMessage updateChatMessage(int id, String updatedContent);

    /**
     * Method to remove a ChatMessage from the Store
     *
     * @param id The ID of the ChatMessage to remove
     */
    void removeChatMessage(int id);
}
