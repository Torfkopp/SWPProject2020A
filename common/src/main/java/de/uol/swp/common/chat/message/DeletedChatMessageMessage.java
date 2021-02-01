package de.uol.swp.common.chat.message;

/**
 * Message sent by the server when a ChatMessage was deleted successfully.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.chat.message.AbstractChatMessageMessage
 * @since 2020-12-17
 */
public class DeletedChatMessageMessage extends AbstractChatMessageMessage {

    private final int id;

    /**
     * Constructor
     * <p>
     * This constructor is used for DeletedChatMessageMessages sent to the global chat.
     * It sets the inherited isLobbyChatMessage to false and sets lobbyName to null.
     *
     * @param id The ID of the deleted ChatMessage
     */
    public DeletedChatMessageMessage(int id) {
        super(null);
        this.id = id;
    }

    /**
     * Constructor
     * <p>
     * This constructor is used for DeletedChatMessageMessages sent to a lobby chat.
     * It sets the inherited isLobbyChatMessage and lobbyName attributes to the
     * parameters provided upon calling the constructor.
     *
     * @param id        The ID of the ChatMessage that was deleted
     * @param lobbyName The Lobby this DeletedChatMessageMessage is being sent to
     *
     * @since 2020-12-30
     */
    public DeletedChatMessageMessage(int id, String lobbyName) {
        super(lobbyName);
        this.id = id;
    }

    /**
     * Gets the ID attribute
     *
     * @return the ID of the deleted ChatMessage
     */
    public int getId() {
        return id;
    }
}
