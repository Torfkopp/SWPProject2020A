package de.uol.swp.client.trade.event;

import de.uol.swp.common.lobby.LobbyName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResetTradeWithBankButtonEventTest {

    @Test
    void getLobbyName() {
        LobbyName lobbyName = new LobbyName("test");
        ResetTradeWithBankButtonEvent event = new ResetTradeWithBankButtonEvent(lobbyName);

        assertEquals(lobbyName, event.getLobbyName());
    }
}