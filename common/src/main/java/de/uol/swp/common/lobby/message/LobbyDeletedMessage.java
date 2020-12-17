package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.UserDTO;

public class LobbyDeletedMessage extends AbstractLobbyMessage {

    public LobbyDeletedMessage(String name, UserDTO user) {
        super(name, user);
    }

}
