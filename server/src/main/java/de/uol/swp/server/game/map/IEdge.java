package de.uol.swp.server.game.map;

/**
 * Interface for an edge
 *
 * @author Mario
 * @since 2021-01-17
 */
public interface IEdge {

    /**
     * Gets the two surrounding intersections
     *
     * @return Array of two positions
     */
    int[] getNeiInt();

    /**
     * Gets neighbouring edges
     *
     * @return Array of positions
     */
    int[] getNeighbours();

    /**
     * Gets the edge's status
     *
     * @return 0 if undeveloped or 1-4 for road owner
     */
    int getState();

    /**
     * Sets the edge's status
     * <p>
     *
     * @param state 1-4 for road owner
     */
    void setState(int state);
}
