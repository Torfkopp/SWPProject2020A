package de.uol.swp.common.chat.dto;

import de.uol.swp.common.chat.SystemMessage;

import java.time.Instant;

public class SystemMessageDTO implements SystemMessage {

    private Instant timestamp;
    private String content;

    public SystemMessageDTO(String content, Instant timestamp) {
        this.timestamp = timestamp;
        this.content = content;
    }

    public SystemMessageDTO(String content) {
        new SystemMessageDTO(content, Instant.now());
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
