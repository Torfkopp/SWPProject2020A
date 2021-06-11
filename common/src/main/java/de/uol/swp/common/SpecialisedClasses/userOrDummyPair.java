package de.uol.swp.common.SpecialisedClasses;

import de.uol.swp.common.user.UserOrDummy;

/**
 * Specialised class to make a pair
 * of two UserOrDummy entities
 *
 * @author Mario Fokken
 * @since 2021-06-11
 */
public class userOrDummyPair {

    UserOrDummy user1;
    UserOrDummy user2;

    /**
     * Constructor
     *
     * @param user1 UserOrDummy
     * @param user2 UserOrDummy
     */
    public userOrDummyPair(UserOrDummy user1, UserOrDummy user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    /**
     * Gets the pair's user1
     *
     * @return UserOrDummy
     */
    public UserOrDummy getUser1() {
        return user1;
    }

    /**
     * Gets the pair's user2
     *
     * @return UserOrDummy
     */
    public UserOrDummy getUser2() {
        return user2;
    }
}
