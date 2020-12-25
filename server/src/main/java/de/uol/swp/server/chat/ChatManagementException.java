package de.uol.swp.server.chat;

/**
 * Exception thrown in ChatManagement
 * <p>
 * This exception is thrown if someone wants to create a ChatMessage without author or content
 * or someone tries to modify or delete a ChatMessage that does not (yet)
 * exist within the ChatMessageStore.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.server.chat.ChatManagement
 * @since 2020-12-16
 */
public class ChatManagementException extends RuntimeException {

    /**
     * Constructor
     *
     * @param s String containing the cause for the exception.
     * @since 2020-12-16
     */
    ChatManagementException(String s) {
        super(s);
    }
}
