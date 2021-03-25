package de.uol.swp.common.util;

/**
 * Class for a Tuple with 3 different types
 *
 * @param <T1> Type of the first value
 * @param <T2> Type of the second value
 * @param <T3> Type of the third value
 *
 * @author Temmo Junkhoff
 * @since 2021-03-19
 */
public class Triple<T1, T2, T3> {

    private final T1 value1;
    private final T2 value2;
    private final T3 value3;

    /**
     * Constructor
     *
     * @param value1 first value
     * @param value2 second value
     * @param value3 third value
     */
    public Triple(T1 value1, T2 value2, T3 value3) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }

    @Override
    public String toString() {
        return "value1 = " + value1 + ", value2 = " + value2 + ", value3 = " + value3;
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

    /**
     * Gets the third value
     *
     * @return The third value
     */
    public T3 getValue3() {
        return value3;
    }
}
