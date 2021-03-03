package de.uol.swp.common.game.map;

/**
 * Interface for an intersection
 *
 * @author Mario Fokken
 * @since 2021-01-17
 */
public interface IIntersection {

    /**
     * Enum for the states of the intersection
     */
    enum IntersectionState {
        FREE,
        BLOCKED,
        SETTLEMENT,
        CITY
    }


    /**
     * Gets the intersection's owner
     *
     * @return The owner of the intersection
     */
    Player getOwner();

    /**
     * Gets the intersection's state
     *
     * @return The state of the intersection
     */
    IntersectionState getState();

    /**
     * Sets the intersection's state
     *
     * @param state The new state of the intersection
     */
    void setState(IntersectionState state);

    /**
     * Sets the intersection's state and owner
     *
     * @param state The new state of the intersection
     * @param owner The new owner of the intersection
     */
    void setOwnerAndState(Player owner, IntersectionState state);
}
