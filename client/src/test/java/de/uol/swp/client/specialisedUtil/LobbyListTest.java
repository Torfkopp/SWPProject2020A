package de.uol.swp.client.specialisedUtil;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyListTest {

    @Test
    void test() {
        LobbyList list = new LobbyList();
        LobbyStringPair pair = new LobbyStringPair(null, null);
        assertTrue(list.get().isEmpty());
        list.add(pair);
        assertFalse(list.get().isEmpty());
        assertEquals(pair, list.get().get(0));
        list.clear();
        assertTrue(list.get().isEmpty());
    }
}
