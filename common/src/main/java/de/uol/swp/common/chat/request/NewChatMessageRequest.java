package de.uol.swp.common.chat.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;

/**
 * Request sent by the client to the server when a new message was typed by the user.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.chat.request.AbstractChatMessageRequest
 * @see de.uol.swp.common.user.User
 * @since 2020-12-17
 */
public class NewChatMessageRequest extends AbstractChatMessageRequest {

    private final User author;
    private final String content;

    /**
     * Constructor
     * <p>
     * This constructor is used for NewChatMessageRequests originating from
     * the global chat. It sets the inherited originLobby attribute to null.
     *
     * @param author  The author of the ChatMessage that should be saved
     * @param content The content of the ChatMessage that should be saved
     */
    public NewChatMessageRequest(User author, String content) {
        super(null);
        this.author = author;
        this.content = content;
    }

    /**
     * Constructor
     * <p>
     * This constructor is used for NewChatMessageRequests originating from
     * a lobby chat. It sets the inherited originLobby attribute to the parameter
     * provided upon calling the constructor.
     *
     * @param author      The author of the ChatMessage that should be saved
     * @param content     The content of the ChatMessage that should be saved
     * @param originLobby The Lobby the NewChatMessageRequest originated from
     *
     * @since 2020-12-30
     */
    public NewChatMessageRequest(User author, String content, LobbyName originLobby) {
        super(originLobby);
        this.author = author;
        this.content = content;
    }

    /**
     * Gets the author attribute
     *
     * @return The author of the ChatMessage that should be saved
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Gets the content attribute
     *
     * @return The content of the ChatMessage that should be saved
     */
    public String getContent() {
        return content;
    }
}
