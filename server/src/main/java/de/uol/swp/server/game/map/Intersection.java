package de.uol.swp.server.game.map;


/**
 * Class for an intersection
 *
 * @author Mario
 * @since 2021-01-17
 */
public class Intersection implements IIntersection {

    private final int[] hexes;
    private final int[] neighbours;
    private String state;

    /**
     * Constructor
     *
     * @param hexes      The surrounding hexes
     * @param neighbours The position of the neighbouring intersections
     * @param state      "f" if free,
     *                   "b" if blocked, or
     *                   1-4 for owner plus s (settlement) or c (city)
     */
    public Intersection(int[] hexes, int[] neighbours, String state) {
        this.hexes = hexes;
        this.neighbours = neighbours;
        this.state = state;
    }

    @Override
    public int[] getHexes() {
        return hexes;
    }

    @Override
    public int[] getNeighbours() {
        return neighbours;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public void setState(String state) {
        this.state = state;
    }
}
