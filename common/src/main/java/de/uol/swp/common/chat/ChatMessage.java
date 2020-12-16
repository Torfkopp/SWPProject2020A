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
     * Getter for the ChatMessage's unique ID
     *
     * @return An int that represents the unique identifier of this ChatMessage
     * @since 2020-12-16
     */
    int getID();

    /**
     * Getter for the ChatMessage's author
     *
     * @return A User object that represents the ChatMessage author#
     * @since 2020-12-16
     */
    User getAuthor();

    /**
     * Getter for the ChatMessage's content
     *
     * @return A String containing the ChatMessage content
     * @since 2020-12-16
     */
    String getContent();

    /**
     * Getter for the ChatMessage's timestamp
     *
     * @return An Instant object in UTC to represent the creation time of the ChatMessage
     * @since 2020-12-16
     */
    Instant getTimestamp();
}
