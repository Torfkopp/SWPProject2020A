package de.uol.swp.common.chat.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

/**
 * Request sent by the client to the server when a new message was typed by the user.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see AbstractRequestMessage
 * @see User
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
    NewChatMessageRequest(User author, String content) {
        this.author = author;
        this.content = content;
    }

    /**
     * Getter for the author attribute
     *
     * @return the author of the ChatMessage that should be saved
     * @since 2020-12-17
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Getter for the content attribute
     *
     * @return the content of the ChatMessage that should be saved
     * @since 2020-12-17
     */
    public String getContent() {
        return content;
    }
}
