package de.uol.swp.common.game.resourceThingies.resource.resource;

import de.uol.swp.common.game.resourceThingies.resource.ResourceType;

import java.io.Serializable;

/**
 * The interface Immutable resource.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public interface IImmutableResource extends Serializable {

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
     * Gets the resource as an immutable resource.
     *
     * @return the immutable
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    ImmutableResource getImmutable();

    /**
     * Gets the type.
     *
     * @return the type
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    ResourceType getType();
}
