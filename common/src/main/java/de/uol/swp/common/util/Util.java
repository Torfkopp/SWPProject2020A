package de.uol.swp.common.util;

import de.uol.swp.common.Colour;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;

import java.util.Random;

/**
 * A utility class
 *
 * @author Temmo Junkhoff
 * @since 2021-06-07
 */
public class Util {

    private static final Random RANDOM = new Random();

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

    /**
     * Returns a randomly selected Colour
     * <p>
     * This excludes the Colour named TEMMO from the results.
     *
     * @return A randomly selected, non-TEMMO Colour
     *
     * @author Phillip-André Suhr
     * @see de.uol.swp.common.Colour
     * @since 2021-06-20
     */
    public static Colour randomColour() {
        return Colour.values()[RANDOM.nextInt(Colour.values().length - 1)];
    }

    /**
     * Returns a randomly selected integer less than {@code bound}
     * <p>
     * The result will always satisfy {@code 0 <= result < bound}.
     *
     * @param bound The (excluded) bound for the integer
     *
     * @return A random integer between 0 (inclusive) and bound (exclusive)
     *
     * @author Phillip-André Suhr
     * @see java.util.Random#nextInt(int)
     * @since 2021-06-20
     */
    public static int randomInt(int bound) {
        return RANDOM.nextInt(bound);
    }

    /**
     * Returns a randomly selected integer
     * <p>
     * The result will always satisfy {@code 0 <= result < 2^31 - 1}.
     *
     * @return A random integer between 0 (inclusive) and 2^31 - 1 (exclusive)
     *
     * @author Phillip-André Suhr
     * @see java.util.Random#nextInt()
     * @since 2021-06-20
     */
    public static int randomInt() {
        return RANDOM.nextInt();
    }

    /**
     * Returns a randomly selected integer larger than 0
     * <p>
     * The result will always satisfy {@code 0 < result < 2^31}, i.e. it will always
     * be larger than 0.
     * <p>
     * This is achieved by adding 1 to a call to {@link Random#nextInt()}.
     *
     * @return A random integer between 0 (exclusive) and 2^31
     *
     * @author Phillip-André Suhr
     * @since 2021-06-20
     */
    public static int randomPositiveInt() {
        return RANDOM.nextInt() + 1;
    }

    /**
     * Returns a randomly selected integer larger than 0 and less than {@code bound}
     * <p>
     * The result will always satisfy {@code 0 < result < bound}, i.e. it will always
     * be larger than 0 and smaller than bound.
     * <p>
     * This is achieved by adding 1 to a call to {@link Random#nextInt()}.
     *
     * @param bound The (excluded) bound for the integer
     *
     * @return A random integer between 0 (exclusive) and bound (exclusive)
     *
     * @author Phillip-André Suhr
     * @see java.util.Random#nextInt(int)
     * @since 2021-06-20
     */
    public static int randomPositiveInt(int bound) {
        return RANDOM.nextInt(bound - 1) + 1;
    }

    /**
     * Returns a randomly selected ResourceType
     * <p>
     * Can return any one of BRICK, GRAIN, LUMBER, ORE, WOOL
     *
     * @return A randomly selected ResourceType
     *
     * @author Phillip-André Suhr
     * @see de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType
     * @since 2021-06-20
     */
    public static ResourceType randomResourceType() {
        return ResourceType.values()[RANDOM.nextInt(ResourceType.values().length)];
    }
}
