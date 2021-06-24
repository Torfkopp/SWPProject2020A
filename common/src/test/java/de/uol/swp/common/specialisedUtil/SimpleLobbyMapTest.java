package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.lobby.LobbyName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleLobbyMapTest {

    @Test
    void test() {
        SimpleLobbyMap simpleLobbyMap = new SimpleLobbyMap();
        LobbyName name = new LobbyName("Test");
        simpleLobbyMap.put(name, null);
        assertTrue(simpleLobbyMap.containsKey(name));
    }
}
