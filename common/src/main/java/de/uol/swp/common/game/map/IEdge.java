package de.uol.swp.common.game.map;

/**
 * Interface for an edge
 *
 * @author Mario Fokken
 * @since 2021-01-17
 */
public interface IEdge {

    /**
     * Builds a road on the edge and sets the road's owner
     * <p>
     *
     * @param owner The owner of the road
     */
    void buildRoad(Player owner);

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
     * Gets the edge's owner or null if undeveloped
     *
     * @return The owner of the road or null if undeveloped
     */
    Player getOwner();
}
