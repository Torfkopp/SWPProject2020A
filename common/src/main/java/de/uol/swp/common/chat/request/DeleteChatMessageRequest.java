package de.uol.swp.common.chat.request;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.user.User;

/**
 * Request sent by the client when a ChatMessage should be deleted.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.chat.request.AbstractChatMessageRequest
 * @since 2020-12-17
 */
public class DeleteChatMessageRequest extends AbstractChatMessageRequest {

    private final int id;
    private final User requestingUser;

    /**
     * Constructor
     * <p>
     * This constructor is used for DeleteChatMessageRequests originating from
     * the global chat. It sets the inherited originLobby attribute to null.
     *
     * @param id             The ID of the ChatMessage that should be deleted
     * @param requestingUser The User who sent the DeleteChatMessageRequest
     */
    public DeleteChatMessageRequest(int id, User requestingUser) {
        super(null);
        this.id = id;
        this.requestingUser = requestingUser;
    }

    /**
     * Constructor
     * <p>
     * This constructor is used for DeleteChatMessageRequests originating from
     * a lobby chat. It sets the inherited originLobby attribute to the parameter
     * provided upon calling the constructor.
     *
     * @param id             The ID of the ChatMessage that should be deleted
     * @param requestingUser The User who sent the DeleteChatMessageRequest
     * @param originLobby    The Lobby the DeleteChatMessageRequest originated from
     *
     * @since 2020-12-30
     */
    public DeleteChatMessageRequest(int id, User requestingUser, LobbyName originLobby) {
        super(originLobby);
        this.id = id;
        this.requestingUser = requestingUser;
    }

    /**
     * Gets the ID attribute
     *
     * @return The ID of the ChatMessage that should be deleted
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the requestingUser attribute.
     *
     * @return The User who sent the DeleteChatMessageRequest
     *
     * @author Maximilian Lindner
     * @author Phillip-André Suhr
     * @since 2021-02-06
     */
    public User getRequestingUser() {
        return requestingUser;
    }
}
