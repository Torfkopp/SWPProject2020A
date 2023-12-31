package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource;

import java.io.Serializable;

/**
 * An interface to model a resource.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public interface IResource extends Serializable {

    /**
     * Copy the resource.
     *
     * @return the mutable resource
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    Resource create();

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
     * Decrease the amount of the resource by 1.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void decrease();

    /**
     * Gets the amount.
     *
     * @return the amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    int getAmount();

    /**
     * Sets the amount of the resource.
     *
     * @param amount the amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void setAmount(int amount);

    /**
     * Gets the type.
     *
     * @return the type
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    ResourceType getType();

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
     * Increase the amount of the resource by 1.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void increase();
}
