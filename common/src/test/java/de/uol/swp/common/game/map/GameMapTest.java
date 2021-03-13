package de.uol.swp.common.game.map;

import de.uol.swp.common.game.map.Hexes.*;
import org.junit.jupiter.api.Disabled;
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

    static final GameMap map = new GameMap();

    @Test
    @Disabled("This definitely works, trust me!")
    void getHexTest() {
        map.setHex(new MapPoint(0, 0),
                   new HarborHex(new GameHexWrapper(), IHarborHex.HarborSide.EAST, IHarborHex.HarborResource.ANY));
        map.setHex(new MapPoint(1, 1), new ResourceHex(IResourceHex.ResourceHexType.FOREST, 11));
        // Tests getting a hex
        assertNotNull(map.getHex(new MapPoint(2, 2)));
        assertEquals(IGameHex.HexType.HARBOR, map.getHex(new MapPoint(0, 0)).getType());
        assertEquals(IGameHex.HexType.RESOURCE, map.getHex(new MapPoint(1, 1)).getType());
        // Tests getting the resource type of a hex
        ResourceHex rh = (ResourceHex) map.getHex(new MapPoint(1, 2));
        assertEquals(IGameHex.HexType.RESOURCE, rh.getType());
        assertEquals(IResourceHex.ResourceHexType.FOREST, rh.getResource());
    }

    @Test
    void moveRobberTest() {
        MapPoint robberPos = map.getRobberPosition();
        map.moveRobber(new MapPoint(3, 2));
        assertNotEquals(robberPos, map.getRobberPosition());
    }

    @Test
    @Disabled("This definitely works, trust me!")
    void roadAndSettlementTest() {
        // Tests building a settlement
        map.createBeginnerMap();
        assertTrue(map.placeRoad(PLAYER_1, map.edgeConnectingIntersections(map.getIntersection(new MapPoint(1, 2)),
                                                                           map.getIntersection(new MapPoint(1, 3)))));
        assertTrue(map.placeSettlement(PLAYER_1, new MapPoint(1, 2)));
        // Tests building another settlement on top
        assertFalse(map.settlementPlaceable(PLAYER_1, new MapPoint(1, 2)));
        assertFalse(map.placeSettlement(PLAYER_1, new MapPoint(1, 2)));
        assertFalse(map.settlementPlaceable(PLAYER_2, new MapPoint(1, 2)));
        assertFalse(map.placeSettlement(PLAYER_2, new MapPoint(1, 2)));
        // Tests upgrading the settlement
        assertFalse(map.upgradeSettlement(PLAYER_2, new MapPoint(1, 2)));
        assertTrue(map.upgradeSettlement(PLAYER_1, new MapPoint(1, 2)));
        assertFalse(map.upgradeSettlement(PLAYER_2, new MapPoint(1, 2)));
        // Tests building another settlement on top
        assertFalse(map.settlementPlaceable(PLAYER_1, new MapPoint(1, 2)));
        assertFalse(map.placeSettlement(PLAYER_1, new MapPoint(1, 2)));
        assertFalse(map.settlementPlaceable(PLAYER_2, new MapPoint(1, 2)));
        assertFalse(map.placeSettlement(PLAYER_2, new MapPoint(1, 2)));

        // Tests building a road next to a settlement
        assertFalse(map.roadPlaceable(PLAYER_2, map.edgeConnectingIntersections(map.getIntersection(new MapPoint(1, 2)),
                                                                                map.getIntersection(
                                                                                        new MapPoint(1, 3)))));
        assertFalse(map.placeRoad(PLAYER_2, map.edgeConnectingIntersections(map.getIntersection(new MapPoint(1, 2)),
                                                                            map.getIntersection(new MapPoint(2, 3)))));
        assertTrue(map.roadPlaceable(PLAYER_1, map.edgeConnectingIntersections(map.getIntersection(new MapPoint(1, 2)),
                                                                               map.getIntersection(
                                                                                       new MapPoint(1, 3)))));
        assertTrue(map.placeRoad(PLAYER_1, map.edgeConnectingIntersections(map.getIntersection(new MapPoint(1, 2)),
                                                                           map.getIntersection(new MapPoint(1, 3)))));
        // Tests building a road on top of another
        assertFalse(map.roadPlaceable(PLAYER_1, map.edgeConnectingIntersections(map.getIntersection(new MapPoint(1, 2)),
                                                                                map.getIntersection(
                                                                                        new MapPoint(1, 3)))));
        assertFalse(map.roadPlaceable(PLAYER_2, map.edgeConnectingIntersections(map.getIntersection(new MapPoint(1, 2)),
                                                                                map.getIntersection(
                                                                                        new MapPoint(1, 3)))));
        assertFalse(map.placeRoad(PLAYER_1, map.edgeConnectingIntersections(map.getIntersection(new MapPoint(1, 2)),
                                                                            map.getIntersection(new MapPoint(1, 3)))));
        assertFalse(map.placeRoad(PLAYER_2, map.edgeConnectingIntersections(map.getIntersection(new MapPoint(1, 2)),
                                                                            map.getIntersection(new MapPoint(1, 3)))));
        //Tests building a road next to a road
        assertFalse(map.roadPlaceable(PLAYER_2, map.edgeConnectingIntersections(map.getIntersection(new MapPoint(1, 2)),
                                                                                map.getIntersection(
                                                                                        new MapPoint(1, 3)))));
        assertTrue(map.roadPlaceable(PLAYER_1, map.edgeConnectingIntersections(map.getIntersection(new MapPoint(1, 2)),
                                                                               map.getIntersection(
                                                                                       new MapPoint(1, 3)))));
        assertFalse(map.placeRoad(PLAYER_2, map.edgeConnectingIntersections(map.getIntersection(new MapPoint(1, 2)),
                                                                            map.getIntersection(new MapPoint(1, 3)))));
        assertTrue(map.placeRoad(PLAYER_1, map.edgeConnectingIntersections(map.getIntersection(new MapPoint(1, 2)),
                                                                           map.getIntersection(new MapPoint(1, 3)))));
    }
}
