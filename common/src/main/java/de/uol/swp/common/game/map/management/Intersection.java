package de.uol.swp.common.game.map.management;

import de.uol.swp.common.game.map.Player;

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
     */
    public Intersection() {
        this.state = IntersectionState.FREE;
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

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
