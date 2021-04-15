package de.uol.swp.common.util;

import java.io.Serializable;

/**
 * Class for a Tuple with 3 different types
 *
 * @param <T1> Type of the first value
 * @param <T2> Type of the second value
 *
 * @author Temmo Junkhoff
 * @since 2021-03-19
 */
public class Tuple<T1, T2> implements Serializable {

    private final T1 value1;
    private final T2 value2;

    /**
     * Constructor
     *
     * @param value1 first value
     * @param value2 second value
     */
    public Tuple(T1 value1, T2 value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    @Override
    public String toString() {
        return "value1 = " + value1 + ", value2 = " + value2;
    }

    /**
     * Gets the first value
     *
     * @return The first value
     */
    public T1 getValue1() {
        return value1;
    }

    /**
     * Gets the second value
     *
     * @return The second value
     */
    public T2 getValue2() {
        return value2;
    }
}
