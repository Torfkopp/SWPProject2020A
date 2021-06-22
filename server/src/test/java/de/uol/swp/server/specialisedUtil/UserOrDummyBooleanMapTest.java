package de.uol.swp.server.specialisedUtil;

import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.AIDTO;
import de.uol.swp.common.user.UserOrDummy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserOrDummyBooleanMapTest {

    @Test
    void test() {
        UserOrDummyBooleanMap map = new UserOrDummyBooleanMap();
        UserOrDummy user = new AIDTO(AI.Difficulty.EASY);
        boolean bool = true;
        map.put(user, bool);
        assertTrue(map.containsKey(user));
        assertTrue(map.containsValue(bool));
        assertEquals(bool, map.get(user));
    }
}
