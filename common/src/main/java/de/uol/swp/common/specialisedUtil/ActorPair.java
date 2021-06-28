package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.user.Actor;

/**
 * Specialised class to make a pair
 * of two Actor entities
 *
 * @author Mario Fokken
 * @since 2021-06-11
 */
public class ActorPair {

    private final Actor actor1;
    private final Actor actor2;

    /**
     * Constructor
     *
     * @param actor1 The pair's first Actor
     * @param actor2 The pair's second Actor
     */
    public ActorPair(Actor actor1, Actor actor2) {
        this.actor1 = actor1;
        this.actor2 = actor2;
    }

    /**
     * Gets the pair's user1
     *
     * @return Actor
     */
    public Actor getActor1() {
        return actor1;
    }

    /**
     * Gets the pair's user2
     *
     * @return Actor
     */
    public Actor getActor2() {
        return actor2;
    }
}
