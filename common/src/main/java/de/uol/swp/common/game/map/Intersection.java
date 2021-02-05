package de.uol.swp.common.game.map;

/**
 * Class for an intersection
 *
 * @author Mario Fokken
 * @since 2021-01-17
 */
public class Intersection implements IIntersection {

    private final int[] hexes;
    private final int[] neighbours;
    private IntersectionState state;
    private Player owner;

    /**
     * Constructor
     *  @param hexes      The surrounding hexes
     * @param neighbours The position of the neighbouring intersections
     */
    public Intersection(int[] hexes, int[] neighbours) {
        this.hexes = hexes;
        this.neighbours = neighbours;
        this.state = IntersectionState.FREE;
    }

    public Intersection(int[] hexes, int[] neighbours, Player owner) {
        this.hexes = hexes;
        this.neighbours = neighbours;
        this.state = IntersectionState.SETTLEMENT;
        this.owner = owner;
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
    public Player getOwner() {
        return owner;
    }

    @Override
    public IntersectionState getState() {
        return state;
    }

    @Override
    public void setState(IntersectionState state) {
        this.state = state;
    }

    @Override
    public void setOwnerAndState(Player owner, IntersectionState state) {
        this.owner = owner;
        this.state = state;
    }
}
