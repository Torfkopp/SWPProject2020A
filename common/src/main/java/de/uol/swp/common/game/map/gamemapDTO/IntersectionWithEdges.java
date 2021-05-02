package de.uol.swp.common.game.map.gamemapDTO;

import java.util.Set;

/**
 * A Class that stores a intersection together with it's surrounding edges
 *
 * @author Aldin Dervisi
 * @author Temmo Junkhoff
 * @since 2021-04-08
 */
public class IntersectionWithEdges implements IIntersectionWithEdges {

    private final IIntersectionWithBuildable intersection;
    private final Set<IEdgeWithBuildable> edges;

    /**
     * Constructor
     *
     * @param intersection The intersection
     * @param edges        The surrounding edges
     */
    public IntersectionWithEdges(IIntersectionWithBuildable intersection, Set<IEdgeWithBuildable> edges) {
        this.intersection = intersection;
        this.edges = edges;
    }

    @Override
    public Set<IEdgeWithBuildable> getEdges() {
        return edges;
    }

    @Override
    public IIntersectionWithBuildable getIntersection() {
        return intersection;
    }
}
