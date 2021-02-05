package de.uol.swp.common.game.map;

/**
 * Interface for an intersection
 *
 * @author Mario Fokken
 * @since 2021-01-17
 */
public interface IIntersection {

    enum IntersectionState {
        FREE,
        BLOCKED,
        SETTLEMENT,
        CITY
    }

    /**
     * Gets the surrounding hexes
     *
     * @return Array of three hex positions
     */
    int[] getHexes();

    /**
     * Gets neighbouring intersections
     *
     * @return Array of positions
     */
    int[] getNeighbours();

    Player getOwner();

    /**
     * Gets the intersection's status
     *
     * @return
     */
    IntersectionState getState();

    void setState(IntersectionState state);

    /**
     * Sets the intersection's status
     *
     * @param state
     */
    void setOwnerAndState(Player owner, IntersectionState state);
}
