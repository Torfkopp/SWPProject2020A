package de.uol.swp.client.chat;

import de.uol.swp.common.user.User;

/**
 * An interface for all methods of the client's ChatService
 * <p>
 * As the communication with the server is based on events, the
 * returns of the call must be handled by events
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see ChatService
 * @since 2020-12-17
 */
public interface IChatService {
    /**
     * Method to create a new ChatMessage
     *
     * @param msg The contents of the new ChatMessage
     * @since 2020-12-17
     */
    void newMessage(User author, String msg);

    /**
     * Method to delete a ChatMessage
     *
     * @param id The ID of the ChatMessage to delete
     * @since 2020-12-17
     */
    void deleteMessage(int id);

    /**
     * Method to change the content of a ChatMessage
     *
     * @param id         The ID of the ChatMessage to edit
     * @param newContent The new content of the ChatMessage
     * @since 2020-12-17
     */
    void editMessage(int id, String newContent);

    /**
     * Ask for a List of [amount] or less ChatMessages that represents the newest [amount]
     * of ChatMessages
     *
     * @param amount The maximum amount of ChatMessages the Client wants to request
     * @since 2020-12-17
     */
    void askLatestMessages(int amount);
}
