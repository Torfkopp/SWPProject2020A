package de.uol.swp.common.game.map;

import java.util.Set;

public class IntersectionWithEdges implements IIntersectionWithEdges {

    IIntersection intersection;
    Set<IEdge> edges;

    public IntersectionWithEdges(IIntersection intersection, Set<IEdge> edges) {
        this.intersection = intersection;
        this.edges = edges;
    }

    @Override
    public Set<IEdge> getEdges() {
        return edges;
    }

    @Override
    public IIntersection getIntersection() {
        return intersection;
    }
}
