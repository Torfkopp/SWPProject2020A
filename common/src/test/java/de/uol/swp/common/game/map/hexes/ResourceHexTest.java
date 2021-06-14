package de.uol.swp.common.game.map.hexes;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceHexTest {

    @Test
    void testResourceHex() {
        ResourceHex hex = new ResourceHex(ResourceType.BRICK, 1);

        assertEquals(IGameHex.HexType.RESOURCE, hex.getType());
        assertEquals(ResourceType.BRICK, hex.getResource());
        assertEquals(1, hex.getToken());
    }
}