package de.uol.swp.server.game.map;

/**
 * Class for an edge
 *
 * @author Mario
 * @since 2021-01-17
 */
public class Edge implements IEdge {

    private int[] neiInt;
    private int[] neighbours;
    private int state;

    /**
     * Constructor
     *
     * @param neiInt     The two neighbouring intersections
     * @param neighbours The two to four neighbouring edges
     * @param state      0 if undeveloped;
     *                   1-4 for road's owner
     */
    public Edge(int[] neiInt, int[] neighbours, int state) {
        this.neiInt = neiInt;
        this.neighbours = neighbours;
        this.state = state;
    }


    @Override
    public int[] getNeiInt() {
        return neiInt;
    }

    @Override
    public int[] getNeighbours() {
        return neighbours;
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }
}
