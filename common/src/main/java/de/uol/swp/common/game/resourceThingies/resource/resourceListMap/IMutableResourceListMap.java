package de.uol.swp.common.game.resourceThingies.resource.resourceListMap;

import de.uol.swp.common.game.resourceThingies.resource.ResourceType;
import de.uol.swp.common.game.resourceThingies.resource.resource.IMutableResource;

import java.util.Iterator;

/**
 * The interface Mutable resource list map.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public interface IMutableResourceListMap extends IResourceListMap, Iterable<IMutableResource> {

    @Override
    Iterator<IMutableResource> iterator();

    /**
     * Clone a mutable resource list map.
     *
     * @return the mutable resource list map
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    IMutableResourceListMap create();

    /**
     * Decrease a resource.
     *
     * @param resource the resource
     * @param amount   the amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void decrease(ResourceType resource, int amount);

    /**
     * Decrease a resource by one.
     *
     * @param resource the resource
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void decrease(ResourceType resource);

    /**
     * Increase a resource.
     *
     * @param resource the resource
     * @param amount   the amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void increase(ResourceType resource, int amount);

    /**
     * Increase a resource by one.
     *
     * @param resource the resource
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void increase(ResourceType resource);

    /**
     * Set the value of a resource.
     *
     * @param resource the resource
     * @param amount   the amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void set(ResourceType resource, int amount);
}
