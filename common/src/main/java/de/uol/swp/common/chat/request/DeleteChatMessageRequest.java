package de.uol.swp.common.chat.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Request sent by the client when a ChatMessage should be deleted.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see AbstractRequestMessage
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
     * Gets the ID attribute
     *
     * @return The ID of the ChatMessage that should be deleted
     * @since 2020-12-17
     */
    public int getId() {
        return id;
    }
}
