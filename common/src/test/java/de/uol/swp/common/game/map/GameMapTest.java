package de.uol.swp.common.game.map;

import de.uol.swp.common.game.map.hexes.IGameHex;
import de.uol.swp.common.game.map.hexes.IHarborHex;
import de.uol.swp.common.game.map.hexes.IResourceHex;
import de.uol.swp.common.game.map.management.GameMapManagement;
import de.uol.swp.common.game.map.management.IGameMapManagement;
import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.game.resourceThingies.resource.ResourceType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.uol.swp.common.game.map.Player.PLAYER_1;
import static de.uol.swp.common.game.map.Player.PLAYER_2;
import static de.uol.swp.common.game.map.management.MapPoint.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This tests the game map
 *
 * @author Mario Fokken
 */
public class GameMapTest {

    private static IGameMapManagement map;

    @BeforeEach
    protected void setUp() {
        map = new GameMapManagement();
        map = map.createMapFromConfiguration(map.getBeginnerConfiguration());
    }

    @AfterEach
    protected void tearDown() {
        map = null;
    }

    @Test
    void getHex_DesertHexType_BeginnerMapTest() {
        IGameHex hex = map.getHex(HexMapPoint(3, 3));
        assertEquals(IGameHex.HexType.DESERT, hex.getType());
    }

    @Test
    void getHex_HarborHex_AnyResource_BeginnerMapTest() {
        IGameHex hex = map.getHex(HexMapPoint(0, 0));
        assertEquals(IGameHex.HexType.HARBOR, hex.getType());
        IHarborHex harborHex = (IHarborHex) hex;
        assertEquals(IHarborHex.HarborResource.ANY, harborHex.getResource());
    }

    @Test
    void getHex_HarborHex_BeginnerMapTest() {
        IGameHex hex = map.getHex(HexMapPoint(2, 0));
        assertEquals(IGameHex.HexType.HARBOR, hex.getType());
        IHarborHex harborHex = (IHarborHex) hex;
        assertEquals(IHarborHex.HarborResource.LUMBER, harborHex.getResource());
    }

    @Test
    void getHex_ResourceHex_BeginnerMapTest() {
        IGameHex hex = map.getHex(HexMapPoint(1, 3));
        assertEquals(IGameHex.HexType.RESOURCE, hex.getType());
        IResourceHex resourceHex = (IResourceHex) hex;
        assertEquals(ResourceType.LUMBER, resourceHex.getResource());
    }

    @Test
    void moveRobberTest() {
        MapPoint robberPos = map.getRobberPosition();
        map.moveRobber(HexMapPoint(3, 2));
        assertNotEquals(robberPos, map.getRobberPosition());
    }

    @Test
    void roadAndSettlementTest() {
        // Tests building a settlement
        map.makeBeginnerSettlementsAndRoads(4);
        assertTrue(map.placeRoad(PLAYER_1,
                                 map.getEdge(EdgeMapPoint(IntersectionMapPoint(1, 3), IntersectionMapPoint(0, 2)))));
        assertFalse(map.placeSettlement(PLAYER_1, IntersectionMapPoint(0, 3)));
        assertTrue(map.placeRoad(PLAYER_1, EdgeMapPoint(IntersectionMapPoint(0, 2), IntersectionMapPoint(0, 3))));
        // Tests building another settlement on top
        assertTrue(map.settlementPlaceable(PLAYER_1, IntersectionMapPoint(0, 3)));
        assertTrue(map.placeSettlement(PLAYER_1, IntersectionMapPoint(0, 3)));
        assertFalse(map.settlementPlaceable(PLAYER_2, IntersectionMapPoint(0, 3)));
        assertFalse(map.placeSettlement(PLAYER_2, IntersectionMapPoint(0, 3)));
        // Tests upgrading the settlement
        assertFalse(map.upgradeSettlement(PLAYER_2, IntersectionMapPoint(0, 3)));
        assertTrue(map.upgradeSettlement(PLAYER_1, IntersectionMapPoint(0, 3)));
        assertFalse(map.upgradeSettlement(PLAYER_2, IntersectionMapPoint(0, 3)));
        // Tests building another settlement on top
        assertFalse(map.settlementPlaceable(PLAYER_1, IntersectionMapPoint(0, 3)));
        assertFalse(map.placeSettlement(PLAYER_1, IntersectionMapPoint(0, 3)));
        assertFalse(map.settlementPlaceable(PLAYER_2, IntersectionMapPoint(0, 3)));
        assertFalse(map.placeSettlement(PLAYER_2, IntersectionMapPoint(0, 3)));

        // Tests building a road next to a settlement
        assertFalse(map.roadPlaceable(PLAYER_2, map.getEdge(
                EdgeMapPoint(IntersectionMapPoint(0, 3), IntersectionMapPoint(0, 4)))));
        assertFalse(map.placeRoad(PLAYER_2,
                                  map.getEdge(EdgeMapPoint(IntersectionMapPoint(0, 3), IntersectionMapPoint(1, 3)))));
        assertTrue(map.roadPlaceable(PLAYER_1, map.getEdge(
                EdgeMapPoint(IntersectionMapPoint(0, 3), IntersectionMapPoint(0, 4)))));
        assertTrue(map.placeRoad(PLAYER_1,
                                 map.getEdge(EdgeMapPoint(IntersectionMapPoint(0, 3), IntersectionMapPoint(0, 4)))));
        // Tests building a road on top of another
        assertFalse(map.roadPlaceable(PLAYER_1, map.getEdge(
                EdgeMapPoint(IntersectionMapPoint(0, 3), IntersectionMapPoint(0, 4)))));
        assertFalse(map.roadPlaceable(PLAYER_2, map.getEdge(
                EdgeMapPoint(IntersectionMapPoint(0, 3), IntersectionMapPoint(0, 4)))));
        assertFalse(map.placeRoad(PLAYER_1,
                                  map.getEdge(EdgeMapPoint(IntersectionMapPoint(0, 3), IntersectionMapPoint(0, 4)))));
        assertFalse(map.placeRoad(PLAYER_2,
                                  map.getEdge(EdgeMapPoint(IntersectionMapPoint(0, 3), IntersectionMapPoint(0, 4)))));
        //Tests building a road next to a road
        assertFalse(map.roadPlaceable(PLAYER_2, map.getEdge(
                EdgeMapPoint(IntersectionMapPoint(0, 3), IntersectionMapPoint(0, 4)))));
        assertFalse(map.placeRoad(PLAYER_2,
                                  map.getEdge(EdgeMapPoint(IntersectionMapPoint(0, 3), IntersectionMapPoint(0, 4)))));
    }
}
