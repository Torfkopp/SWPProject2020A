package de.uol.swp.common.game.map.hexes;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HarbourHexTest {

    @Test
    void testHarbourHex() {
        GameHexWrapper gameHexWrapper = new GameHexWrapper();
        gameHexWrapper.set(new ResourceHex(ResourceType.BRICK, 2));

        HarbourHex hex = new HarbourHex(gameHexWrapper, IHarbourHex.HarbourSide.NORTHEAST,
                                        IHarbourHex.HarbourResource.BRICK);

        assertEquals(gameHexWrapper, hex.getBelongingHex());
        assertEquals(IHarbourHex.HarbourResource.BRICK, hex.getResource());
        assertEquals(IHarbourHex.HarbourSide.NORTHEAST, hex.getSide());
        assertEquals(IGameHex.HexType.HARBOUR, hex.getType());
    }
}