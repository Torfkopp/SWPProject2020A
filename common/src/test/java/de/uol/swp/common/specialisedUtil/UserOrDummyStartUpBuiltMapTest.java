package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.game.StartUpPhaseBuiltStructures;
import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.AIDTO;
import de.uol.swp.common.user.UserOrDummy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserOrDummyStartUpBuiltMapTest {

    @Test
    void test() {
        UserOrDummyStartUpBuiltMap map = new UserOrDummyStartUpBuiltMap();
        UserOrDummy user = new AIDTO(AI.Difficulty.EASY);
        map.put(user);
        assertTrue(map.containsKey(user));
        assertTrue(map.containsValue(StartUpPhaseBuiltStructures.NONE_BUILT));
        assertEquals(StartUpPhaseBuiltStructures.NONE_BUILT, map.get(user));
        map.nextPhase(user);
        assertEquals(StartUpPhaseBuiltStructures.FIRST_BOTH_BUILT, map.get(user));
        map.nextPhase(user);
        assertEquals(StartUpPhaseBuiltStructures.ALL_BUILT, map.get(user));
        assertTrue(map.finished(user));
    }
}
