package de.uol.swp.common.chat.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Request sent by the client when a ChatMessage should be updated.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.message.AbstractRequestMessage
 * @since 2020-12-17
 */
public class EditChatMessageRequest extends AbstractRequestMessage {
    private final int id;
    private final String content;

    /**
     * Constructor
     *
     * @param id      The id of the ChatMessage that should be updated
     * @param content The content of the ChatMessage that should be updated
     * @since 2020-12-17
     */
    public EditChatMessageRequest(int id, String content) {
        this.id = id;
        this.content = content;
    }

    /**
     * Getter for the ID attribute
     *
     * @return the ID of the ChatMessage that should be updated
     * @since 2020-12-17
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the content attribute
     *
     * @return the content of the ChatMessage that should be updated
     * @since 2020-12-17
     */
    public String getContent() {
        return content;
    }
}
