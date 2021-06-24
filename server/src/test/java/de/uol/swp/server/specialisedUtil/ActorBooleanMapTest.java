package de.uol.swp.server.specialisedUtil;

import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.AIDTO;
import de.uol.swp.common.user.Actor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActorBooleanMapTest {

    @Test
    void test() {
        ActorBooleanMap map = new ActorBooleanMap();
        Actor user = new AIDTO(AI.Difficulty.EASY);
        boolean bool = true;
        map.put(user, bool);
        assertTrue(map.containsKey(user));
        assertTrue(map.containsValue(bool));
        assertEquals(bool, map.get(user));
    }
}
