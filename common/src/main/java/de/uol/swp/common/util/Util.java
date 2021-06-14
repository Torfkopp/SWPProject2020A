package de.uol.swp.common.util;

/**
 * A utility class
 *
 * @author Temmo Junkhoff
 * @since 2021-06-07
 */
public class Util {

    /**
     * Compares two objects and returns if they are equals.
     * This method is null-safe and returns false if the first object is null
     *
     * @param a The first object that should be compared
     * @param b The first object that should be compared
     *
     * @return True if they are equals, false otherwise or if a is null
     */
    public static boolean equals(Object a, Object b) {
        if (a == null) return false;
        return a.equals(b);
    }
}
