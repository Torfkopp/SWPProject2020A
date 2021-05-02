package de.uol.swp.common.game.map.management;

import de.uol.swp.common.game.map.Player;

/**
 * Class for an edge
 *
 * @author Mario Fokken
 * @since 2021-01-17
 */
public class Edge implements IEdge {

    private final Orientation orientation;
    private Player owner;

    /**
     * Constructor for a edge without a road on it
     */
    public Edge(Orientation orientation) {
        this(orientation, null);
    }

    /**
     * Constructor for a edge with a road on it
     *
     * @param owner The owner of the road on the edge
     */
    public Edge(Orientation orientation, Player owner) {
        this.orientation = orientation;
        this.owner = owner;
    }

    @Override
    public void buildRoad(Player owner) {
        this.owner = owner;
    }

    @Override
    public Orientation getOrientation() {return orientation;}

    @Override
    public Player getOwner() {
        return owner;
    }
}
