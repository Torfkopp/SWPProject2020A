package de.uol.swp.common.chat.message;

import de.uol.swp.common.message.AbstractServerMessage;

/**
 * Message sent by the server when a ChatMessage got edited successfully
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see AbstractServerMessage
 * @since 2020-12-17
 */
public class EditedChatMessageMessage extends AbstractServerMessage {
    private final int id;
    private final String newContent;
    // TODO: send whole ChatMessage object

    /**
     * Constructor
     *
     * @param id         The ID of the edited ChatMessage
     * @param newContent The new content of the edited ChatMessage
     * @since 2020-12-17
     */
    public EditedChatMessageMessage(int id, String newContent) {
        this.id = id;
        this.newContent = newContent;
    }

    /**
     * Getter for the ID attribute
     *
     * @return the ID of the edited ChatMessage
     * @since 2020-12-17
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the newContent attribute
     *
     * @return the new Content of the edited ChatMessage
     * @since 2020-12-17
     */
    public String getNewContent() {
        return newContent;
    }
}
