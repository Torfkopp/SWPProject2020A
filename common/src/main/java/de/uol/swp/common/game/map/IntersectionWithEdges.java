package de.uol.swp.common.game.map;

import java.util.Set;

/**
 * A Class that stores a intersection together with it's surrounding edges
 *
 * @author Aldin Dervisi
 * @author Temmo Junkhoff
 * @since 2021-04-08
 */
public class IntersectionWithEdges implements IIntersectionWithEdges {

    IIntersection intersection;
    Set<IEdge> edges;

    /**
     * Constructor
     * @param intersection The intersection
     * @param edges The surrounding edges
     */
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
