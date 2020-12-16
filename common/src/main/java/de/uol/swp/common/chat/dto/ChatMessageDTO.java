package de.uol.swp.common.chat.dto;

import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.user.User;

import java.time.Instant;

/**
 * Objects of this class are used to transfer chat message data between the server and clients
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.chat.ChatMessage
 * @since 2020-12-16
 */
public class ChatMessageDTO implements ChatMessage {

    private final Integer id;
    private final User author;
    private final Instant timestamp;
    private final String content;

    /**
     * Constructor
     *
     * @param id        The unique ID of the new ChatMessage
     * @param author    The User who wrote the message
     * @param timestamp The Instant timestamp when the message was created
     * @param content   The content of the message
     */
    public ChatMessageDTO(int id, User author, Instant timestamp, String content) {
        this.id = id;
        this.author = author;
        this.timestamp = timestamp;
        this.content = content;
    }

    /**
     * Constructor without timestamp
     * <p>
     * Adds an Instant timestamp to the object at creation
     *
     * @param id      The unique ID of the new ChatMessage
     * @param author  The User who wrote the message
     * @param content The content of the message
     */
    public ChatMessageDTO(int id, User author, String content) {
        this.id = id;
        this.author = author;
        this.timestamp = Instant.now();
        this.content = content;
    }

    /**
     * Copy constructor
     *
     * @param chatMessage ChatMessage object to copy the values of
     * @return ChatMessageDTO copy of ChatMessage object
     * @since 2020-12-16
     */
    public static ChatMessageDTO create(ChatMessage chatMessage) {
        return new ChatMessageDTO(chatMessage.getID(), chatMessage.getAuthor(), chatMessage.getContent());
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public User getAuthor() {
        return this.author;
    }

    @Override
    public Instant getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessageDTO that = (ChatMessageDTO) o;
        return id.equals(that.id) && author.equals(that.author) && timestamp.equals(that.timestamp) && content.equals(that.content);
    }

    @Override
    public int compareTo(ChatMessage o) {
        return id.compareTo(o.getID());
    }

    @Override
    public String toString() {
        return "ChatMessageDTO{" +
                "id=" + id +
                ", author=" + author.getUsername() +
                ", timestamp=" + timestamp +
                ", content='" + content + '\'' +
                '}';
    }
}
