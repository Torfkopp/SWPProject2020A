package de.uol.swp.common.chat.message;

import de.uol.swp.common.message.AbstractServerMessage;

/**
 * Message sent by the server when a ChatMessage was deleted successfully.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see AbstractServerMessage
 * @since 2020-12-17
 */
public class DeletedChatMessageMessage extends AbstractServerMessage {
    private final int id;

    /**
     * Constructor
     *
     * @param id The ID of the deleted ChatMessage
     * @since 2020-12-17
     */
    public DeletedChatMessageMessage(int id) {
        this.id = id;
    }

    /**
     * Gets the ID attribute
     *
     * @return the ID of the deleted ChatMessage
     * @since 2020-12-17
     */
    public int getId() {
        return id;
    }
}
