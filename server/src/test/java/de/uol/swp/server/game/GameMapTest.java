package de.uol.swp.server.game;

import de.uol.swp.server.game.map.GameMapManagement;
import de.uol.swp.server.game.map.Hexes.IGameHex;
import de.uol.swp.server.game.map.Hexes.IResourceHex;
import de.uol.swp.server.game.map.Hexes.ResourceHex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This tests the game map
 *
 * @author Mario
 */
public class GameMapTest {

    static final GameMapManagement map = new GameMapManagement();

    @Test
    void getHexTest() {
        // Tests getting a hex
        assertNotNull(map.getHex(2));
        assertEquals(map.getHex(1).getType(), IGameHex.type.Harbor);
        assertEquals(map.getHex(19).getType(), IGameHex.type.Resource);
        // Tests getting the resource type of a hex
        ResourceHex rh = (ResourceHex) map.getHex(20);
        assertEquals(rh.getType(), IGameHex.type.Resource);
        assertEquals(rh.getResource(), IResourceHex.resource.Forest);
    }

    @Test
    void roadAndSettlementTest() {
        // Tests building a settlement
        assertTrue(map.placeSettlement(1, 1));
        // Tests building another settlement on top
        assertFalse(map.settlementPlaceable(1, 1));
        assertFalse(map.placeSettlement(1, 1));
        assertFalse(map.settlementPlaceable(2, 1));
        assertFalse(map.placeSettlement(2, 1));
        // Tests upgrading the settlement
        assertFalse(map.upgradeSettlement(2, 1));
        assertTrue(map.upgradeSettlement(1, 1));
        assertFalse(map.upgradeSettlement(2, 1));
        // Tests building another settlement on top
        assertFalse(map.settlementPlaceable(1, 1));
        assertFalse(map.placeSettlement(1, 1));
        assertFalse(map.settlementPlaceable(2, 1));
        assertFalse(map.placeSettlement(2, 1));

        // Tests building a road next to a settlement
        assertFalse(map.roadPlaceable(2, 1));
        assertFalse(map.placeRoad(2, 1));
        assertTrue(map.roadPlaceable(1, 1));
        assertTrue(map.placeRoad(1, 1));
        // Tests building a road on top of another
        assertFalse(map.roadPlaceable(1, 1));
        assertFalse(map.roadPlaceable(2, 1));
        assertFalse(map.placeRoad(1, 1));
        assertFalse(map.placeRoad(2, 1));
        //Tests building a road next to a road
        assertFalse(map.roadPlaceable(2, 2));
        assertTrue(map.roadPlaceable(1, 2));
        assertFalse(map.placeRoad(2, 2));
        assertTrue(map.placeRoad(1, 2));
    }

    @Test
    void moveRobberTest() {
        int robberPos = map.getRobberPos();
        map.moveRobber(36);
        assertNotEquals(robberPos, map.getRobberPos());
    }

}
