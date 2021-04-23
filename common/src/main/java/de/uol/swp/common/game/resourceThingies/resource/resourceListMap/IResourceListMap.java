package de.uol.swp.common.game.resourceThingies.resource.resourceListMap;

import de.uol.swp.common.game.resourceThingies.resource.ResourceType;
import de.uol.swp.common.game.resourceThingies.resource.resource.IImmutableResource;

/**
 * The interface Resource list map.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public interface IResourceListMap{

    /**
     * Get the resource as an immutable resource.
     *
     * @param resource the resource
     *
     * @return the immutable resource
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    IImmutableResource get(ResourceType resource);

    /**
     * Gets the amount of a specific resource.
     *
     * @param resource the resource
     *
     * @return the amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    int getAmount(ResourceType resource);
}
