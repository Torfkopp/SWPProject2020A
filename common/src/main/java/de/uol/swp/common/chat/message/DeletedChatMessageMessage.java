package de.uol.swp.common.chat.message;

import de.uol.swp.common.message.AbstractServerMessage;

/**
 * Message sent by the server when a ChatMessage was successfully deleted.
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
     * @param id The ID of the ChatMessage that was deleted
     * @since 2020-12-17
     */
    public DeletedChatMessageMessage(int id) {
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
