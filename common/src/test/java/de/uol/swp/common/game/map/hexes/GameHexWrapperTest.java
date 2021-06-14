package de.uol.swp.common.game.map.hexes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameHexWrapperTest {

    @Test
    void testGameHexWrapper() {
        GameHexWrapper hexWrapper = new GameHexWrapper();

        assertEquals(IGameHex.HexType.WATER, hexWrapper.get().getType());

        DesertHex desertHex = new DesertHex();
        hexWrapper.set(desertHex);

        assertEquals(desertHex, hexWrapper.get());
        assertEquals(IGameHex.HexType.DESERT, hexWrapper.get().getType());
    }
}