package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.user.Actor;

/**
 * Specialised class to make a pair
 * of two Actor entities
 *
 * @author Mario Fokken
 * @since 2021-06-11
 */
public class actorPair {

    private final Actor user1;
    private final Actor user2;

    /**
     * Constructor
     *
     * @param user1 The pair's first Actor
     * @param user2 The pair's second Actor
     */
    public actorPair(Actor user1, Actor user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    /**
     * Gets the pair's user1
     *
     * @return Actor
     */
    public Actor getUser1() {
        return user1;
    }

    /**
     * Gets the pair's user2
     *
     * @return Actor
     */
    public Actor getUser2() {
        return user2;
    }
}
