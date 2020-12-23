package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.User;

public class LobbyDeletedMessage extends AbstractLobbyMessage {

    public LobbyDeletedMessage(String name, User user) {
        super(name, user);
    }

}
