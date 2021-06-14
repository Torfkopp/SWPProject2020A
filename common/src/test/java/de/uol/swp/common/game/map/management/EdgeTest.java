package de.uol.swp.common.game.map.management;

import de.uol.swp.common.game.map.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EdgeTest {

    @Test
    void testEdge() {
        Edge edge = new Edge(IEdge.Orientation.SOUTH);

        assertEquals(IEdge.Orientation.SOUTH, edge.getOrientation());

        assertNull(edge.getOwner());

        edge.buildRoad(Player.PLAYER_1);

        assertEquals(Player.PLAYER_1, edge.getOwner());
    }

    @Test
    void testEdgeWithOwner() {
        Edge edge = new Edge(IEdge.Orientation.WEST, Player.PLAYER_1);

        assertEquals(IEdge.Orientation.WEST, edge.getOrientation());
        assertEquals(Player.PLAYER_1, edge.getOwner());
    }
}