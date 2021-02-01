package de.uol.swp.common.game.map;

/**
 * Interface for an intersection
 *
 * @author Mario Fokken
 * @since 2021-01-17
 */
public interface IIntersection {

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

    /**
     * Gets the intersection's status
     *
     * @return "f" if free, "b" if blocked, or 1-4 for owner plus s (settlement) or c (city)
     */
    String getState();

    /**
     * Sets the intersection's status
     *
     * @param state "f" if free,
     *              "b" if blocked, or
     *              1-4 for owner plus s (settlement) or c (city)
     */
    void setState(String state);
}
