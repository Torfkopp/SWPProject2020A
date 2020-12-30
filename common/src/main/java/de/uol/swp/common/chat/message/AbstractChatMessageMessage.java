package de.uol.swp.common.chat.message;

import de.uol.swp.common.message.AbstractServerMessage;

public abstract class AbstractChatMessageMessage extends AbstractServerMessage {
    private final boolean isLobbyChatMessage;
    private final String lobbyName;

    public AbstractChatMessageMessage(boolean isLobbyChatMessage) {
        this.isLobbyChatMessage = isLobbyChatMessage;
        this.lobbyName = null;
    }

    public AbstractChatMessageMessage(boolean isLobbyChatMessage, String lobbyName) {
        this.isLobbyChatMessage = isLobbyChatMessage;
        this.lobbyName = lobbyName;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public boolean isLobbyChatMessage() {
        return isLobbyChatMessage;
    }
}
