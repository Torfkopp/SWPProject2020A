package de.uol.swp.common.chat.request;

import de.uol.swp.common.message.AbstractRequestMessage;

public abstract class AbstractChatMessageRequest extends AbstractRequestMessage {
    private final boolean fromLobby;
    private final String originLobby;

    public AbstractChatMessageRequest(String originLobby) {
        this.originLobby = originLobby;
        this.fromLobby = (originLobby != null);
    }

    public boolean isFromLobby() {
        return fromLobby;
    }

    public String getOriginLobby() {
        return originLobby;
    }
}
