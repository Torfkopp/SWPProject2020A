package de.uol.swp.common.user;

public interface UserOrDummy extends Comparable<UserOrDummy> {

    /**
     * Gets the ID variable
     *
     * @return ID of the user
     *
     * @author Aldin Dervisi
     * @author Phillip-André Suhr
     * @since 2021-02-23
     */
    int getID();

    /**
     * Gets the username variable
     *
     * @return Username of the user as a string
     *
     * @since 2019-08-05
     */
    String getUsername();
}
