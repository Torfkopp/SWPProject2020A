package de.uol.swp.common.game.map;

/**
 * Interface for an edge
 *
 * @author Mario Fokken
 * @since 2021-01-17
 */
public interface IEdge {

    /**
     * Gets neighbouring edges
     *
     * @return Array of positions
     */
    int[] getNeighbouringEdges();

    /**
     * Gets the two surrounding intersections
     *
     * @return Array of two positions
     */
    int[] getNeighbouringIntersections();

    /**
     * Gets the edge's status
     *
     * @return
     */
    Player getOwner();

    /**
     * Sets the edge's status
     * <p>
     *
     * @param owner
     */
    void setOwner(Player owner);
}
