package de.uol.swp.common.chat.dto;

import de.uol.swp.common.chat.SystemMessage;

import java.time.Instant;

/**
 * Objects of this class are used to transfer SystemMessages between the server
 * and clients
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.chat.SystemMessage
 * @since 2021-02-22
 */
public class SystemMessageDTO implements SystemMessage {

    private final Instant timestamp;
    private final String content;

    /**
     * Constructor
     *
     * @param content   The content of the message
     * @param timestamp The Instant timestamp when the message was created
     */
    public SystemMessageDTO(String content, Instant timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }

    /**
     * Constructor
     *
     * @param content The content of the message
     */
    public SystemMessageDTO(String content) {
        this(content, Instant.now());
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return content;
    }
}
