package de.uol.swp.common.game.map.management;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MapPointTest {

    @Test
    void edgeMapPoint() {
        MapPoint l = MapPoint.HexMapPoint(1, 1);
        MapPoint r = MapPoint.HexMapPoint(1, 2);
        MapPoint mapPoint = MapPoint.EdgeMapPoint(l, r);

        assertEquals(MapPoint.Type.EDGE, mapPoint.getType());
        assertEquals(l, mapPoint.getL());
        assertEquals(r, mapPoint.getR());
    }

    @Test
    void hexMapPoint() {
        MapPoint mapPoint = MapPoint.HexMapPoint(1, 3);

        assertEquals(MapPoint.Type.HEX, mapPoint.getType());
        assertEquals(1, mapPoint.getY());
        assertEquals(3, mapPoint.getX());
        assertNull(mapPoint.getL());
        assertNull(mapPoint.getR());
    }

    @Test
    void intersectionMapPoint() {
        MapPoint mapPoint = MapPoint.IntersectionMapPoint(2, 4);

        assertEquals(MapPoint.Type.INTERSECTION, mapPoint.getType());
        assertEquals(2, mapPoint.getY());
        assertEquals(4, mapPoint.getX());
        assertNull(mapPoint.getL());
        assertNull(mapPoint.getR());
    }

    @Test
    void invalidMapPoint() {
        MapPoint mapPoint = MapPoint.InvalidMapPoint();

        assertEquals(MapPoint.Type.INVALID, mapPoint.getType());
        assertNull(mapPoint.getL());
        assertNull(mapPoint.getR());
    }
}