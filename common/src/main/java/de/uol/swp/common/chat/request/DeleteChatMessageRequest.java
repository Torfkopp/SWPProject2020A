package de.uol.swp.common.chat.request;

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

    /**
     * Constructor
     * <p>
     * This constructor is used for DeleteChatMessageRequests originating from
     * the global chat. It sets the inherited originLobby attribute to null.
     *
     * @param id The ID of the ChatMessage that should be deleted
     */
    public DeleteChatMessageRequest(int id) {
        super(null);
        this.id = id;
    }

    /**
     * Constructor
     * <p>
     * This constructor is used for DeleteChatMessageRequests originating from
     * a lobby chat. It sets the inherited originLobby attribute to the parameter
     * provided upon calling the constructor.
     *
     * @param id          The ID of the ChatMessage that should be deleted
     * @param originLobby The Lobby the DeleteChatMessageRequest originated from
     * @since 2020-12-30
     */
    public DeleteChatMessageRequest(int id, String originLobby) {
        super(originLobby);
        this.id = id;
    }

    /**
     * Gets the ID attribute
     *
     * @return The ID of the ChatMessage that should be deleted
     */
    public int getId() {
        return id;
    }
}
