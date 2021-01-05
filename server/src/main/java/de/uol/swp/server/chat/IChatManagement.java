package de.uol.swp.server.chat;

import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.user.User;

import java.util.List;

/**
 * An interface for all methods of the ChatManagement
 *
 * @author Temmo Junkhoff
 * @author Phillip -André Suhr
 * @see de.uol.swp.server.chat.AbstractChatManagement
 * @see de.uol.swp.server.chat.ChatManagement
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.chat.ChatMessage
 * @since 2020-12-16
 */
public interface IChatManagement {

    /**
     * Returns a List with ChatMessage objects of size {@literal <amount>} or smaller
     * from the global Chat Store
     *
     * @param amount The amount of ChatMessages to be returned
     * @return A List of ChatMessage objects of size {@literal <amount>} or smaller
     */
    List<ChatMessage> getLatestMessages(int amount);

    /**
     * Returns a List with ChatMessage objects of size {@literal <amount>} or smaller
     * from a Lobby Chat Store
     *
     * @param amount      The amount of ChatMessages to be returned
     * @param originLobby The Lobby the latest ChatMessages should be retrieved for
     * @return A List of ChatMessage objects of size {@literal <amount>} or smaller
     * @since 2020-12-30
     */
    List<ChatMessage> getLatestMessages(int amount, String originLobby);

    /**
     * Create a new ChatMessage in the ChatMessageStore of the global Chat Store
     *
     * @param author  The author of the ChatMessage
     * @param content The content of the ChatMessage
     * @return The created ChatMessage object
     */
    ChatMessage createChatMessage(User author, String content);

    /**
     * Create a new ChatMessage in the ChatMessageStore of a Lobby's Chat Store
     *
     * @param author      The author of the ChatMessage
     * @param content     The content of the ChatMessage
     * @param originLobby The Lobby the ChatMessage was sent in
     * @return The created ChatMessage object
     * @since 2020-12-30
     */
    ChatMessage createChatMessage(User author, String content, String originLobby);

    /**
     * Update content of ChatMessage in the global Chat Store
     *
     * @param id             The id of the ChatMessage to update
     * @param updatedContent The new content of the ChatMessage
     * @return The updated ChatMessage object
     */
    ChatMessage updateChatMessage(int id, String updatedContent);

    /**
     * Update content of ChatMessage in a Lobby Chat Store
     *
     * @param id             The id of the ChatMessage to update
     * @param updatedContent The new content of the ChatMessage
     * @param originLobby    The Lobby the ChatMessage was sent in
     * @return The updated ChatMessage object
     * @since 2020-12-30
     */
    ChatMessage updateChatMessage(int id, String updatedContent, String originLobby);

    /**
     * Delete a ChatMessage from the global Chat Store
     *
     * @param id The ID of the ChatMessage that should be deleted
     */
    void dropChatMessage(int id);

    /**
     * Delete a ChatMessage from a Lobby Chat Store
     *
     * @param id          The ID of the ChatMessage that should be deleted
     * @param originLobby The Lobby the ChatMessage was sent in
     * @since 2020-12-30
     */
    void dropChatMessage(int id, String originLobby);
}
