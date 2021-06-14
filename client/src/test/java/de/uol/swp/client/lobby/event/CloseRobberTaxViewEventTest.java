package de.uol.swp.client.lobby.event;

import de.uol.swp.common.lobby.LobbyName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CloseRobberTaxViewEventTest {

    @Test
    void getLobbyName() {
        LobbyName lobbyName = new LobbyName("test lobby");
        CloseRobberTaxViewEvent event = new CloseRobberTaxViewEvent(lobbyName);

        assertEquals(lobbyName, event.getLobbyName());
    }
}