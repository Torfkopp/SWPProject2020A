package de.uol.swp.common.game.map.hexes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DesertHexTest {

    @Test
    void getType() {
        DesertHex hex = new DesertHex();

        assertEquals(IGameHex.HexType.DESERT, hex.getType());
    }
}