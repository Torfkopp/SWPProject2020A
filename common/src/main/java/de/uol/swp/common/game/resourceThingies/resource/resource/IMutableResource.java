package de.uol.swp.common.game.resourceThingies.resource.resource;

import java.io.Serializable;

/**
 * The interface Mutable resource.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public interface IMutableResource extends Serializable, IImmutableResource {

    /**
     * Copy the mutable resource.
     *
     * @return the mutable resource
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    MutableResource create();

    /**
     * Decrease the amount of the resource.
     *
     * @param amount the amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void decrease(int amount);

    /**
     * Decrease the amount of the resource.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void decrease();

    /**
     * Increase the amount of the resource.
     *
     * @param amount the amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void increase(int amount);

    /**
     * Increase the amount of the resource.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void increase();

    /**
     * Sets the amount of the resource.
     *
     * @param amount the amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void setAmount(int amount);
}
