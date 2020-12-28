package de.uol.swp.server.chat;

import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.user.User;

import java.util.List;

/**
 * An interface for all methods of the ChatManagement
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.server.chat.AbstractChatManagement
 * @see de.uol.swp.server.chat.ChatManagement
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.chat.ChatMessage
 * @since 2020-12-16
 */
public interface IChatManagement {

    /**
     * Returns a List with ChatMessage objects of size [amount] or smaller
     *
     * @param amount The amount of ChatMessages to be returned
     * @return a List of ChatMessage objects of size [amount] or smaller
     * @since 2020-12-16
     */
    List<ChatMessage> getLatestMessages(int amount);

    /**
     * Create a new ChatMessage in the ChatMessageStore
     *
     * @param author  the author of the ChatMessage
     * @param content the content of the ChatMessage
     * @return the created ChatMessage
     * @since 2020-12-16
     */
    ChatMessage createChatMessage(User author, String content);

    /**
     * Update content of ChatMessage.
     *
     * @param id             the id of the ChatMessage to update
     * @param updatedContent the new content of the ChatMessage
     * @return the updated ChatMessage
     * @since 2020-12-16
     */
    ChatMessage updateChatMessage(int id, String updatedContent);

    /**
     * Delete a ChatMessage.
     *
     * @param id the ID of the ChatMessage that should be deleted
     * @since 2020-12-16
     */
    void dropChatMessage(int id);
}
