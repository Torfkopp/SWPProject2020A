package de.uol.swp.common.chat.dto;

import de.uol.swp.common.chat.SystemMessage;

import java.time.Instant;

public class SystemMessageDTO implements SystemMessage {

    private final Instant timestamp;
    private final String content;

    public SystemMessageDTO(String content, Instant timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }

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
