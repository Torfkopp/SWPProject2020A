package de.uol.swp.common.chat.message;

/**
 * Message sent by the server when a ChatMessage was successfully deleted.
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
     *
     * @param id The ID of the ChatMessage that was deleted
     * @since 2020-12-17
     */
    public DeletedChatMessageMessage(int id) {
        super(false);
        this.id = id;
    }

    public DeletedChatMessageMessage(int id, boolean isLobbyChatMessage, String lobbyName) {
        super(isLobbyChatMessage, lobbyName);
        this.id = id;
    }

    /**
     * Getter for the ID attribute
     *
     * @return the ID of the ChatMessage that got deleted
     * @since 2020-12-17
     */
    public int getId() {
        return id;
    }
}
