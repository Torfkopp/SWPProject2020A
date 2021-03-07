package de.uol.swp.common.game.map;

/**
 * Interface for an edge
 *
 * @author Mario Fokken
 * @since 2021-01-17
 */
public interface IEdge {

    /**
     * Enum for the orientation of the edge
     */
    enum Orientation {
        WEST,
        EAST,
        SOUTH
    }

    /**
     * Builds a road on the edge and sets the road's owner
     * <p>
     *
     * @param owner The owner of the road
     */
    void buildRoad(Player owner);

    /**
     * Returns the orientation of the edge
     *
     * @return The orientation of the edge
     */
    Orientation getOrientation();

    /**
     * Gets the edge's owner or null if undeveloped
     *
     * @return The owner of the road or null if undeveloped
     */
    Player getOwner();
}
