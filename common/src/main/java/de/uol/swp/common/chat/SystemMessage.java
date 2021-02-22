package de.uol.swp.common.chat;

import java.time.Instant;

public interface SystemMessage extends ChatOrSystemMessage {

    public String getContent();

    public Instant getTimestamp();
}
