package de.uol.swp.common.game.map.gamemapDTO;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class IntersectionWithEdgesTest {

    @Test
    void testIntersectionWithEdge() {
        EdgeWithBuildable edge = mock(EdgeWithBuildable.class);
        EdgeWithBuildable edge2 = mock(EdgeWithBuildable.class);
        Set<IEdgeWithBuildable> edgeSet = new HashSet<>();
        edgeSet.add(edge);
        edgeSet.add(edge2);

        IntersectionWithBuildable intersection = mock(IntersectionWithBuildable.class);

        IntersectionWithEdges intersectionWithEdges = new IntersectionWithEdges(intersection, edgeSet);

        assertEquals(edgeSet, intersectionWithEdges.getEdges());
        assertEquals(intersection, intersectionWithEdges.getIntersection());
    }
}