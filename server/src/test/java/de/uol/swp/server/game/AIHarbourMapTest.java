package de.uol.swp.server.game;

import de.uol.swp.common.game.map.hexes.IHarbourHex;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.AIDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static de.uol.swp.common.game.map.hexes.IHarbourHex.HarbourResource.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AIHarbourMapTest {

    @Test
    void test() {
        AIHarbourMap map = new AIHarbourMap();
        AI ai = new AIDTO(AI.Difficulty.EASY);
        List<IHarbourHex.HarbourResource> list = new ArrayList<>();
        map.put(ai, list);
        assertNull(map.tradeGet(ai, 0));
        assertNull(map.tradeGet(ai, 0));
        list.add(LUMBER);
        list.add(GRAIN);
        map.put(ai, list);
        assertEquals(map.tradeGet(ai, 0), ResourceType.LUMBER);
        assertEquals(map.tradeGet(ai, 15), ResourceType.GRAIN);
        list.add(BRICK);
        list.add(ORE);
        map.put(ai, list);
        assertEquals(map.tradeGet(ai, 0), ResourceType.BRICK);
        assertEquals(map.tradeGet(ai, 15), ResourceType.ORE);
        list.clear();
        list.add(WOOL);
        map.put(ai, list);
        assertEquals(map.tradeGet(ai, 0), ResourceType.WOOL);
        assertEquals(map.tradeGet(ai, 15), ResourceType.WOOL);
    }
}
