package de.uol.swp.server.game.map;


/**
 * Class for an intersection
 *
 * @author Mario
 * @since 2021-01-17
 */
public class intersection implements iGameMapManagement.iIntersection {

    private int[] neighbours;
    private String state;

    /**
     * Constructor
     *
     * @param neighbours The position of the neighbouring intersections
     * @param state      "f" if free,
     *                   "b" if blocked, or
     *                   1-4 for owner plus s (settlement) or c (city)
     */
    public intersection(int[] neighbours, String state) {
        this.neighbours = neighbours;
        this.state = state;
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
