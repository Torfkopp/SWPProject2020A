package de.uol.swp.common.game.message;

import de.uol.swp.common.lobby.message.AbstractLobbyMessage;
import de.uol.swp.common.user.UserOrDummy;

public class UnpauseTimerMessage extends AbstractLobbyMessage {

    private String lobbyName;

    public UnpauseTimerMessage(String lobbyName, UserOrDummy user) {
        super(lobbyName, user);
    }

    public String getLobbyName() {
        return lobbyName;
    }
}
