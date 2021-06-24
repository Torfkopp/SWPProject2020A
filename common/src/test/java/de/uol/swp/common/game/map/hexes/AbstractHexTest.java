package de.uol.swp.common.game.map.hexes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractHexTest {

    @Test
    void testAbstractHex() {
        AbstractHex hex = new AbstractHex() {
            @Override
            public HexType getType() {
                return HexType.DESERT;
            }
        };

        hex.setRobberOnField(true);

        assertTrue(hex.isRobberOnField());

        hex.setRobberOnField(false);

        assertFalse(hex.isRobberOnField());
    }
}