package de.uol.swp.common.game.map;

/**
 * Class for an intersection
 *
 * @author Mario Fokken
 * @since 2021-01-17
 */
public class Intersection implements IIntersection {

    private IntersectionState state;
    private Player owner;

    /**
     * Constructor for an free intersection
     *
     */
    public Intersection() {
        this.state = IntersectionState.FREE;
    }

    /**
     * Constructor for an intersection with settlement on it
     *
     * @param hexes      The surrounding hexes
     * @param neighbours The position of the neighbouring intersections
     * @param owner      The owner of the settlement
     */
    public Intersection(Player owner) {
        this.state = IntersectionState.SETTLEMENT;
        this.owner = owner;
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
