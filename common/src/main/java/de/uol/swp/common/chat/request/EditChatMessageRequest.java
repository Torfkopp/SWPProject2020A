package de.uol.swp.common.chat.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;

/**
 * Request sent by the client when a ChatMessage should be updated.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.chat.request.AbstractChatMessageRequest
 * @since 2020-12-17
 */
public class EditChatMessageRequest extends AbstractChatMessageRequest {

    private final int id;
    private final String content;
    private final User requestingUser;

    /**
     * Constructor
     * <p>
     * This constructor is used for EditChatMessageRequests originating from
     * the global chat. It sets the inherited originLobby attribute to null.
     *
     * @param id             The ID of the ChatMessage that should be updated
     * @param content        The content of the ChatMessage that should be updated
     * @param requestingUser The User who sent the EditChatMessageRequest
     */
    public EditChatMessageRequest(int id, String content, User requestingUser) {
        super(null);
        this.id = id;
        this.content = content;
        this.requestingUser = requestingUser;
    }

    /**
     * Constructor
     * <p>
     * This constructor is used for EditChatMessageRequests originating from
     * a lobby chat. It sets the inherited originLobby attribute to the parameter
     * provided upon calling the constructor.
     *
     * @param id             The ID of the ChatMessage that should be edited
     * @param content        The content of the ChatMessage that should be updated
     * @param requestingUser The User who sent the EditChatMessageRequest
     * @param originLobby    The Lobby the EditChatMessageRequest originated from
     *
     * @since 2020-12-30
     */
    public EditChatMessageRequest(int id, String content, User requestingUser, LobbyName originLobby) {
        super(originLobby);
        this.id = id;
        this.content = content;
        this.requestingUser = requestingUser;
    }

    /**
     * Gets the content attribute
     *
     * @return The content of the ChatMessage that should be updated
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the ID attribute
     *
     * @return The ID of the ChatMessage that should be updated
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the requestingUser attribute.
     *
     * @return The User who sent the EditChatMessageRequest
     *
     * @author Maximilian Lindner
     * @author Phillip-André Suhr
     * @since 2021-02-06
     */
    public User getRequestingUser() {
        return requestingUser;
    }
}
