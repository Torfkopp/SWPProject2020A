package de.uol.swp.common.game.map.management;

import de.uol.swp.common.game.map.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntersectionTest {

    @Test
    void testIntersection() {
        Intersection intersection = new Intersection();

        assertEquals(IIntersection.IntersectionState.FREE, intersection.getState());
        assertNull(intersection.getOwner());

        intersection.setState(IIntersection.IntersectionState.SETTLEMENT);

        assertEquals(IIntersection.IntersectionState.SETTLEMENT, intersection.getState());

        intersection.setOwnerAndState(Player.PLAYER_1, IIntersection.IntersectionState.CITY);

        assertEquals(Player.PLAYER_1, intersection.getOwner());
        assertEquals(IIntersection.IntersectionState.CITY, intersection.getState());
    }

    @Test
    void testIntersectionEquals() {
        Intersection intersection = new Intersection();
        Intersection other = new Intersection();

        assertNotEquals(intersection, other);
        assertEquals(intersection, intersection);
        assertEquals(other, other);
    }
}