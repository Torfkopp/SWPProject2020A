package de.uol.swp.common.game.map;

/**
 * Class for an edge
 *
 * @author Mario Fokken
 * @since 2021-01-17
 */
public class Edge implements IEdge {

    private final int[] neighbouringIntersections;
    private final int[] neighbouringEdges;
    private Player owner;

    /**
     * Constructor for a edge without a road on it
     *
     * @param neighbouringIntersections The two neighbouring intersections
     * @param neighbouringEdges         The two to four neighbouring edges
     */
    public Edge(int[] neighbouringIntersections, int[] neighbouringEdges) {
        this.neighbouringIntersections = neighbouringIntersections;
        this.neighbouringEdges = neighbouringEdges;
        this.owner = null;
    }

    /**
     * Constructor for a edge with a road on it
     *
     * @param neighbouringIntersections The two neighbouring intersections
     * @param neighbouringEdges         The two to four neighbouring edges
     * @param owner                     The owner of the road on the edge
     */
    public Edge(int[] neighbouringIntersections, int[] neighbouringEdges, Player owner) {
        this.neighbouringIntersections = neighbouringIntersections;
        this.neighbouringEdges = neighbouringEdges;
        this.owner = owner;
    }

    @Override
    public void buildRoad(Player owner) {
        this.owner = owner;
    }

    @Override
    public int[] getNeighbouringEdges() {
        return neighbouringEdges;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public int[] getNeighbouringIntersections() {
        return neighbouringIntersections;
    }
}
