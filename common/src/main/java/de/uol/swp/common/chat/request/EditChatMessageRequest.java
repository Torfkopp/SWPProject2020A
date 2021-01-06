package de.uol.swp.common.chat.request;

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

    /**
     * Constructor
     * <p>
     * This constructor is used for EditChatMessageRequests originating from
     * the global chat. It sets the inherited originLobby attribute to null.
     *
     * @param id      The ID of the ChatMessage that should be updated
     * @param content The content of the ChatMessage that should be updated
     */
    public EditChatMessageRequest(int id, String content) {
        super(null);
        this.id = id;
        this.content = content;
    }

    /**
     * Constructor
     * <p>
     * This constructor is used for EditChatMessageRequests originating from
     * a lobby chat. It sets the inherited originLobby attribute to the parameter
     * provided upon calling the constructor.
     *
     * @param id          The ID of the ChatMessage that should be edited
     * @param content     The content of the ChatMessage that should be updated
     * @param originLobby The Lobby the EditChatMessageRequest originated from
     * @since 2020-12-30
     */
    public EditChatMessageRequest(int id, String content, String originLobby) {
        super(originLobby);
        this.id = id;
        this.content = content;
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
     * Gets the content attribute
     *
     * @return The content of the ChatMessage that should be updated
     */
    public String getContent() {
        return content;
    }
}
