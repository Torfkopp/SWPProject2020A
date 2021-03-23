package de.uol.swp.common.user;

/**
 * An interface mainly used to group users, dummys and AIs
 */
public interface UserOrDummy extends Comparable<UserOrDummy> {

    /**
     * Gets the ID of the user or dummy
     *
     * @return ID of the user
     *
     * @author Aldin Dervisi
     * @author Phillip-André Suhr
     * @since 2021-02-23
     */
    int getID();

    /**
     * Gets the username of the user or dummy
     *
     * @return Username of the user as a string
     *
     * @since 2019-08-05
     */
    String getUsername();
}
