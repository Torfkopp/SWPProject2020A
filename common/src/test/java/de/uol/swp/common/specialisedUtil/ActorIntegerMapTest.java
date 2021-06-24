package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.AIDTO;
import de.uol.swp.common.user.Actor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActorIntegerMapTest {

    @Test
    void test() {
        ActorIntegerMap map = new ActorIntegerMap();
        Actor user = new AIDTO(AI.Difficulty.EASY);
        int number = 42;
        map.put(user, number);
        assertTrue(map.containsKey(user));
        assertTrue(map.containsValue(number));
        assertEquals(number, map.get(user));
    }
}
