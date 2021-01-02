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
 * @see de.uol.swp.client.chat.ChatService
 * @since 2020-12-17
 */
public interface IChatService {
    /**
     * Method to create a new ChatMessage
     *
     * @param author The User who authored the ChatMessage
     * @param msg    The contents of the new ChatMessage
     */
    void newMessage(User author, String msg);

    /**
     * Method to create a new ChatMessage in a lobbyChat
     *
     * @param author      The User who authored the ChatMessage
     * @param msg         The contents of the new ChatMessage
     * @param originLobby The name of the Lobby where the ChatMessage was sent
     * @since 2020-12-30
     */
    void newMessage(User author, String msg, String originLobby);

    /**
     * Method to delete a ChatMessage
     *
     * @param id The ID of the ChatMessage to delete
     */
    void deleteMessage(int id);

    /**
     * Method to delete a ChatMessage in a lobbyChat
     *
     * @param id          The ID of the ChatMessage to delete
     * @param originLobby The name of the Lobby where the ChatMessage was sent
     * @since 2020-12-30
     */
    void deleteMessage(int id, String originLobby);

    /**
     * Method to change the content of a ChatMessage
     *
     * @param id         The ID of the ChatMessage to edit
     * @param newContent The new content of the ChatMessage
     */
    void editMessage(int id, String newContent);

    /**
     * Method to change the content of a ChatMessage in a lobbyChat
     *
     * @param id          The ID of the ChatMessage to edit
     * @param newContent  The new content of the ChatMessage
     * @param originLobby The name of the Lobby where the ChatMessage was sent
     * @since 2020-12-30
     */
    void editMessage(int id, String newContent, String originLobby);

    /**
     * Ask for a List of {@literal <amount>} or less ChatMessages that represents the newest {@literal <amount>}
     * of ChatMessages
     *
     * @param amount The maximum amount of ChatMessages the Client wants to request
     */
    void askLatestMessages(int amount);

    /**
     * Ask for a List of {@literal <amount>} or less ChatMessages that represents the newest {@literal <amount>}
     * of ChatMessages
     *
     * @param amount      The maximum amount of ChatMessages the Client wants to request
     * @param originLobby The name of the Lobby where the ChatMessages were sent
     * @since 2020-12-30
     */
    void askLatestMessages(int amount, String originLobby);
}
