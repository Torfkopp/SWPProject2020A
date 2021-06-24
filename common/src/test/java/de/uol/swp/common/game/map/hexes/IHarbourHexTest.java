package de.uol.swp.common.game.map.hexes;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IHarbourHexTest {

    @Test
    void getHarbourResource() {
        assertEquals(IHarbourHex.HarbourResource.GRAIN, IHarbourHex.getHarbourResource(ResourceType.GRAIN));
        assertEquals(IHarbourHex.HarbourResource.BRICK, IHarbourHex.getHarbourResource(ResourceType.BRICK));
        assertEquals(IHarbourHex.HarbourResource.ORE, IHarbourHex.getHarbourResource(ResourceType.ORE));
        assertEquals(IHarbourHex.HarbourResource.LUMBER, IHarbourHex.getHarbourResource(ResourceType.LUMBER));
        assertEquals(IHarbourHex.HarbourResource.WOOL, IHarbourHex.getHarbourResource(ResourceType.WOOL));
        assertThrows(NullPointerException.class, () -> IHarbourHex.getHarbourResource(null));
    }
}