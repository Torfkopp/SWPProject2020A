package de.uol.swp.common.lobby.response;

import de.uol.swp.common.message.AbstractResponseMessage;

public class CreateLobbyResponse extends AbstractResponseMessage {

    private final String name;

    public CreateLobbyResponse(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
