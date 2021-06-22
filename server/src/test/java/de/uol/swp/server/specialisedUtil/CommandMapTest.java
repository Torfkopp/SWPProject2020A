package de.uol.swp.server.specialisedUtil;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandMapTest {

    @Test
    void test() {
        CommandMap map = new CommandMap();
        String s = "Test";
        map.put(s, null);
        assertTrue(map.containsKey(s));
        assertNull(map.get(s));
    }
}
