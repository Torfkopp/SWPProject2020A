package de.uol.swp.common.game.map;

/**
 * Class for an edge
 *
 * @author Mario Fokken
 * @since 2021-01-17
 */
public class Edge implements IEdge {

    private Player owner;

    /**
     * Constructor for a edge without a road on it
     */
    public Edge() {
        this.owner = null;
    }

    /**
     * Constructor for a edge with a road on it
     *
     * @param owner The owner of the road on the edge
     */
    public Edge(Player owner) {
        this.owner = owner;
    }

    @Override
    public void buildRoad(Player owner) {
        this.owner = owner;
    }

    @Override
    public Player getOwner() {
        return owner;
    }
}
