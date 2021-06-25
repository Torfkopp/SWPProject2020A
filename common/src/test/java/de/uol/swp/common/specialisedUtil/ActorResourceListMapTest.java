package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.AIDTO;
import de.uol.swp.common.user.Actor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActorResourceListMapTest {

    @Test
    void test() {
        ActorResourceListMap map = new ActorResourceListMap();
        Actor user = new AIDTO(AI.Difficulty.EASY);
        ResourceList list = new ResourceList();
        map.put(user, list);
        assertTrue(map.containsKey(user));
        assertTrue(map.containsValue(list));
    }
}
