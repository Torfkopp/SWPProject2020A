package de.uol.swp.common.chat.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Request sent by the client when a ChatMessage should be deleted.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.message.AbstractRequestMessage
 * @since 2020-12-17
 */
public class DeleteChatMessageRequest extends AbstractRequestMessage {
    private final int id;

    /**
     * Constructor
     *
     * @param id The ID of the ChatMessage that should be deleted
     * @since 2020-12-17
     */
    public DeleteChatMessageRequest(int id) {
        this.id = id;
    }

    /**
     * Getter for the ID attribute
     *
     * @return the ID of the ChatMessage that should be deleted
     * @since 2020-12-17
     */
    public int getId() {
        return id;
    }
}
