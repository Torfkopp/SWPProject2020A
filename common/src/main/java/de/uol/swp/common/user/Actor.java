package de.uol.swp.common.user;

/**
 * An interface mainly used to group users, dummys, and AIs
 *
 * @author Alwin Bossert
 * @author Temmo Junkhoff
 * @since 2021-03-14
 */
public interface Actor extends Comparable<Actor> {

    /**
     * Gets the ID of the actor
     *
     * @return ID of the user
     *
     * @since 2021-03-14
     */
    int getID();

    /**
     * Gets the username of the actor
     *
     * @return Username of the user as a string
     *
     * @since 2021-03-14
     */
    String getUsername();
}
