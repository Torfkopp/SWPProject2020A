package de.uol.swp.common.chat.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

/**
 * Request sent by the client to the server when a new message was typed by the user.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.message.AbstractRequestMessage
 * @see de.uol.swp.common.user.User
 * @since 2020-12-17
 */
public class NewChatMessageRequest extends AbstractRequestMessage {
    private final User author;
    private final String content;

    /**
     * Constructor
     *
     * @param author  The author of the ChatMessage that should be saved
     * @param content The content of the ChatMessage that should be saved
     * @since 2020-12-17
     */
    public NewChatMessageRequest(User author, String content) {
        this.author = author;
        this.content = content;
    }

    /**
     * Gets the author attribute
     *
     * @return The author of the ChatMessage that should be saved
     * @since 2020-12-17
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Gets the content attribute
     *
     * @return The content of the ChatMessage that should be saved
     * @since 2020-12-17
     */
    public String getContent() {
        return content;
    }
}
