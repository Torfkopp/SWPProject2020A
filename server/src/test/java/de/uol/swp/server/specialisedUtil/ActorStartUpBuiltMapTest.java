package de.uol.swp.server.specialisedUtil;

import de.uol.swp.common.game.StartUpPhaseBuiltStructures;
import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.AIDTO;
import de.uol.swp.common.user.Actor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActorStartUpBuiltMapTest {

    @Test
    void test() {
        ActorStartUpBuiltMap map = new ActorStartUpBuiltMap();
        Actor user = new AIDTO(AI.Difficulty.EASY);
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
