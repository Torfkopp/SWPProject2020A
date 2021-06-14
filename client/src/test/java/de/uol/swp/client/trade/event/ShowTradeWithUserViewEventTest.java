package de.uol.swp.client.trade.event;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ShowTradeWithUserViewEventTest {

    @Test
    void testShowTradeWithUserViewEvent() {
        LobbyName lobbyName = mock(LobbyName.class);
        UserOrDummy userOrDummy = mock(UserOrDummy.class);
        var event = new ShowTradeWithUserViewEvent(lobbyName, userOrDummy, true);

        assertEquals(lobbyName, event.getLobbyName());
        assertEquals(userOrDummy, event.getRespondingUser());
        assertTrue(event.isCounterOffer());
    }
}