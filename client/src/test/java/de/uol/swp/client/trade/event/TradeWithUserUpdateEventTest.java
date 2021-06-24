package de.uol.swp.client.trade.event;

import de.uol.swp.common.lobby.LobbyName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TradeWithUserUpdateEventTest {

    @Test
    void getLobbyName() {
        LobbyName lobbyName = new LobbyName("test");
        TradeWithUserUpdateEvent event = new TradeWithUserUpdateEvent(lobbyName);

        assertEquals(lobbyName, event.getLobbyName());
    }
}