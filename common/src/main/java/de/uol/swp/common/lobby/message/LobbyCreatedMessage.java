package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.User;

public class LobbyCreatedMessage extends AbstractLobbyMessage {

    public LobbyCreatedMessage(String name, User user) {
        super(name, user);
    }
}
