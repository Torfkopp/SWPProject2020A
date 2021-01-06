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
     * Method to retrieve a ChatMessage from the global ChatStore through its ID
     *
     * @param id The ID to look up
     * @return The ChatMessage object with that ID if found
     */
    Optional<ChatMessage> findMessage(int id);

    /**
     * Method to retrieve a ChatMessage from a lobby's ChatStore through its ID
     *
     * @param id          The ID to look up
     * @param originLobby The lobby the searched ChatMessage was sent in
     * @return The ChatMessage object with that ID if found
     * @since 2020-12-30
     */
    Optional<ChatMessage> findMessage(int id, String originLobby);

    /**
     * Method to get the last {@literal <amount>} Messages
     *
     * @param amount Amount of ChatMessages to be retrieved
     * @return List of latest ChatMessages, sorted newest to oldest
     */
    List<ChatMessage> getLatestMessages(int amount);

    /**
     * Method to get the last {@literal <amount>} Messages
     *
     * @param amount      Amount of ChatMessages to be retrieved
     * @param originLobby The lobby the latest ChatMessages should be retrieved for
     * @return List of latest ChatMessages, sorted newest to oldest
     * @since 2020-12-30
     */
    List<ChatMessage> getLatestMessages(int amount, String originLobby);

    /**
     * Method to create a ChatMessage in the global Chat Store
     *
     * @param author  The User who wrote the ChatMessage
     * @param content The content of the message
     * @return The created ChatMessage object
     * @implSpec ID and Timestamp need to be added on creation
     */
    ChatMessage createChatMessage(User author, String content);

    /**
     * Method to create a ChatMessage in a lobby's ChatStore
     *
     * @param author      The user who wrote the ChatMessage
     * @param content     The content of the message
     * @param originLobby The lobby the ChatMessage was sent in
     * @return The created ChatMessage object
     * @implSpec ID and Timestamp need to be added on creation
     * @since 2020-12-30
     */
    ChatMessage createChatMessage(User author, String content, String originLobby);

    /**
     * Method to update contents of a ChatMessage in the global Chat Store
     *
     * @param id             The ChatMessage ID to update
     * @param updatedContent The new content of the message
     * @return The updated ChatMessage object
     */
    ChatMessage updateChatMessage(int id, String updatedContent);

    /**
     * Method to update contents of a ChatMessage in a Lobby's Chat Store
     *
     * @param id             The ChatMessage ID to update
     * @param updatedContent The new content of the message
     * @param originLobby    The lobby the ChatMessage was sent in
     * @return The updated ChatMessage object
     * @since 2020-12-30
     */
    ChatMessage updateChatMessage(int id, String updatedContent, String originLobby);

    /**
     * Method to remove a ChatMessage from the global Chat Store
     *
     * @param id The ID of the ChatMessage to remove
     */
    void removeChatMessage(int id);

    /**
     * Method to remove a ChatMessage from a Lobby's Chat Store
     *
     * @param id          The ID of the ChatMessage to remove
     * @param originLobby The lobby the ChatMessage was sent in
     * @since 2020-12-30
     */
    void removeChatMessage(int id, String originLobby);
}
