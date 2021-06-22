package de.uol.swp.client.specialisedUtil;

import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.SimpleLobby;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LobbyListItemTest {

    @Test
    void test() {
        ISimpleLobby lobby = new SimpleLobby(new LobbyName("Test"), false, null, 0, 0, false, false, false, null, null,
                                             0);
        String s = "Test";
        LobbyListItem pair = new LobbyListItem(lobby, s);
        assertEquals(lobby, pair.getKey());
        assertEquals(s, pair.getValue());
    }
}
