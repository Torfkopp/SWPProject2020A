package de.uol.swp.common.game.map;

import java.io.Serializable;
import java.util.Set;

/**
 * An interface for a class to store an intersection with it's surrounding edges
 *
 * @author Aldin Dervisi
 * @author Temmo Junkhoff
 * @since 2021-04-08
 */
public interface IIntersectionWithEdges extends Serializable {

    /**
     * Gets the edges surrounding the intersection
     *
     * @return The Set of edges surround the intersection
     */
    Set<IEdge> getEdges();

    /**
     * Gets The intersection
     *
     * @return The intersection
     */
    IIntersection getIntersection();
}
