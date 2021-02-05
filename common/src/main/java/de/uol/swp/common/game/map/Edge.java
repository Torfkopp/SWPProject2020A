package de.uol.swp.common.game.map;

/**
 * Class for an edge
 *
 * @author Mario Fokken
 * @since 2021-01-17
 */
public class Edge implements IEdge {

    private final int[] neiInt;
    private final int[] neighbours;
    private Player owner;

    /**
     * Constructor
     *
     * @param neiInt     The two neighbouring intersections
     * @param neighbours The two to four neighbouring edges
     */
    public Edge(int[] neiInt, int[] neighbours) {
        this.neiInt = neiInt;
        this.neighbours = neighbours;
        this.owner = null;
    }

    public Edge(int[] neiInt, int[] neighbours, Player owner) {
        this.neiInt = neiInt;
        this.neighbours = neighbours;
        this.owner = owner;
    }

    @Override
    public int[] getNeighbouringEdges() {
        return neighbours;
    }

    @Override
    public int[] getNeighbouringIntersections() {
        return neiInt;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
