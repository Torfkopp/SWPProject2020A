package de.uol.swp.common.chat;

import de.uol.swp.common.user.User;

import java.io.Serializable;
import java.time.Instant;

/**
 * Interface to unify ChatMessage objects
 * <p>
 * This interface allows for different types of ChatMessage since
 * not every client needs all information of all messages.
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.chat.dto.ChatMessageDTO
 * @since 2020-12-16
 */
public interface ChatMessage extends Serializable, Comparable<ChatMessage> {

    /**
     * Gets the ChatMessage's author
     *
     * @return A User object that represents the ChatMessage author#
     *
     * @since 2020-12-16
     */
    User getAuthor();

    /**
     * Gets the ChatMessage's content
     *
     * @return A String containing the ChatMessage content
     *
     * @since 2020-12-16
     */
    String getContent();

    /**
     * Sets the ChatMessage's content
     *
     * @param newContent The new ChatMessage content
     *
     * @implNote This method also automatically sets the edited attribute to true!
     * @since 2020-12-16
     */
    void setContent(String newContent);

    /**
     * Getter for the ChatMessage's unique ID
     *
     * @return An int that represents the unique identifier of this ChatMessage
     *
     * @since 2020-12-16
     */
    int getID();

    /**
     * Gets the ChatMessage's timestamp
     *
     * @return An Instant object in UTC to represent the creation time of the ChatMessage
     *
     * @since 2020-12-16
     */
    Instant getTimestamp();

    /**
     * Gets the ChatMessage's edited status
     *
     * @return False if the ChatMessage wasn't edited. True if it was edited.
     *
     * @since 2020-12-19
     */
    boolean isEdited();
}
