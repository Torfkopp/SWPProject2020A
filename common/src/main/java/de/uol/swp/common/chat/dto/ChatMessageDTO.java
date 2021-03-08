package de.uol.swp.common.chat.dto;

import com.google.common.base.Strings;
import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.user.User;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Objects of this class are used to transfer chat message data between the
 * server and clients
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.chat.ChatMessage
 * @since 2020-12-16
 */
public class ChatMessageDTO implements ChatMessage {

    private final Integer id;
    private final User author;
    private final Instant timestamp;
    private String content;
    private boolean edited;

    /**
     * Constructor
     *
     * @param id        The unique ID of the new ChatMessage
     * @param author    The User who wrote the message
     * @param timestamp The Instant timestamp when the message was created
     * @param content   The content of the message
     */
    public ChatMessageDTO(int id, User author, Instant timestamp, String content) {
        this(id, author, timestamp, content, false);
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
        this(id, author, Instant.now(), content, false);
    }

    /**
     * Constructor with edited attribute
     *
     * @param id        The unique ID of the new ChatMessage
     * @param author    The User who wrote the message
     * @param timestamp The Instant timestamp when the message was created
     * @param content   The content of the message
     * @param edited    The edit status of the ChatMessage
     */
    public ChatMessageDTO(int id, User author, Instant timestamp, String content, boolean edited) {
        if (!Objects.nonNull(author)) throw new IllegalArgumentException("Author must not be null");
        if (Strings.isNullOrEmpty(content)) throw new IllegalArgumentException("Content must not be null or empty");
        if (!Objects.nonNull(timestamp)) throw new IllegalArgumentException("Timestamp must not be null");
        this.id = id;
        this.author = author;
        this.timestamp = timestamp;
        this.content = content;
        this.edited = edited;
    }

    /**
     * Copy constructor
     *
     * @param chatMessage ChatMessage object to copy the values of
     *
     * @return Copy of ChatMessage object
     *
     * @since 2020-12-16
     */
    public static ChatMessage create(ChatMessage chatMessage) {
        return new ChatMessageDTO(chatMessage.getID(), chatMessage.getAuthor(), chatMessage.getTimestamp(),
                                  chatMessage.getContent(), chatMessage.isEdited());
    }

    /**
     * Converts a Instant (Timestamp) to a string
     *
     * @param timestamp The Instant to convert
     *
     * @return String The created string
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see de.uol.swp.common.chat.ChatMessage
     * @since 2020-12-17
     */
    private static String timestampToString(Instant timestamp) {
        return String.format("%02d", timestamp.atZone(ZoneOffset.systemDefault()).getHour()) + ":" + String
                .format("%02d", timestamp.atZone(ZoneOffset.systemDefault()).getMinute());
    }

    @Override
    public User getAuthor() {
        return this.author;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public void setContent(String newContent) {
        if (newContent == null) return;
        this.content = newContent;
        this.edited = true;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public Instant getTimestamp() {
        return this.timestamp;
    }

    @Override
    public boolean isEdited() {
        return this.edited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessageDTO) o;
        return id.equals(that.getID()) && author.equals(that.getAuthor()) && timestamp
                .equals(that.getTimestamp()) && content.equals(that.getContent());
    }

    /**
     * Converts a ChatMessage to a string
     *
     * @return String The created string
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see de.uol.swp.common.chat.ChatMessage
     * @since 2020-12-17
     */
    @Override
    public String toString() {
        String text = this.getContent() + " - " + this.getAuthor().getUsername() + " - " + timestampToString(
                this.getTimestamp());
        if (isEdited()) text += " (ed)";
        return text;
    }

    @Override
    public int compareTo(ChatMessage o) {
        return id.compareTo(o.getID());
    }
}
