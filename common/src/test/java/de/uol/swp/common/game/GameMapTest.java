package de.uol.swp.common.game;

import de.uol.swp.common.game.map.GameMapManagement;
import de.uol.swp.common.game.map.Hexes.IGameHex;
import de.uol.swp.common.game.map.Hexes.IResourceHex;
import de.uol.swp.common.game.map.Hexes.ResourceHex;
import org.junit.jupiter.api.Test;

import static de.uol.swp.common.game.map.Player.PLAYER_1;
import static de.uol.swp.common.game.map.Player.PLAYER_2;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This tests the game map
 *
 * @author Mario Fokken
 */
public class GameMapTest {

    static final GameMapManagement map = new GameMapManagement();

    @Test
    void getHexTest() {
        // Tests getting a hex
        assertNotNull(map.getHex(2));
        assertEquals(map.getHex(1).getType(), IGameHex.HexType.HARBOR);
        assertEquals(map.getHex(19).getType(), IGameHex.HexType.RESOURCE);
        // Tests getting the resource type of a hex
        ResourceHex rh = (ResourceHex) map.getHex(20);
        assertEquals(rh.getType(), IGameHex.HexType.RESOURCE);
        assertEquals(rh.getResource(), IResourceHex.ResourceHexType.FOREST);
    }

    @Test
    void moveRobberTest() {
        int robberPos = map.getRobberPos();
        map.moveRobber(36);
        assertNotEquals(robberPos, map.getRobberPos());
    }

    @Test
    void roadAndSettlementTest() {
        // Tests building a settlement
        assertTrue(map.placeSettlement(PLAYER_1, 1));
        // Tests building another settlement on top
        assertFalse(map.settlementPlaceable(PLAYER_1, 1));
        assertFalse(map.placeSettlement(PLAYER_1, 1));
        assertFalse(map.settlementPlaceable(PLAYER_2, 1));
        assertFalse(map.placeSettlement(PLAYER_2, 1));
        // Tests upgrading the settlement
        assertFalse(map.upgradeSettlement(PLAYER_2, 1));
        assertTrue(map.upgradeSettlement(PLAYER_1, 1));
        assertFalse(map.upgradeSettlement(PLAYER_2, 1));
        // Tests building another settlement on top
        assertFalse(map.settlementPlaceable(PLAYER_1, 1));
        assertFalse(map.placeSettlement(PLAYER_1, 1));
        assertFalse(map.settlementPlaceable(PLAYER_2, 1));
        assertFalse(map.placeSettlement(PLAYER_2, 1));

        // Tests building a road next to a settlement
        assertFalse(map.roadPlaceable(PLAYER_2, 1));
        assertFalse(map.placeRoad(PLAYER_2, 1));
        assertTrue(map.roadPlaceable(PLAYER_1, 1));
        assertTrue(map.placeRoad(PLAYER_1, 1));
        // Tests building a road on top of another
        assertFalse(map.roadPlaceable(PLAYER_1, 1));
        assertFalse(map.roadPlaceable(PLAYER_2, 1));
        assertFalse(map.placeRoad(PLAYER_1, 1));
        assertFalse(map.placeRoad(PLAYER_2, 1));
        //Tests building a road next to a road
        assertFalse(map.roadPlaceable(PLAYER_2, 2));
        assertTrue(map.roadPlaceable(PLAYER_1, 2));
        assertFalse(map.placeRoad(PLAYER_2, 2));
        assertTrue(map.placeRoad(PLAYER_1, 2));
    }
}
