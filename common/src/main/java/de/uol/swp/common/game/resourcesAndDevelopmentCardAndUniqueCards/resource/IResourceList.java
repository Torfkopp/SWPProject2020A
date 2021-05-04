package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource;

import java.io.Serializable;
import java.util.*;

/**
 * The interface Mutable resource list map.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public interface IResourceList extends Iterable<IResource>, Serializable {

    static List<Map<String, Object>> getTableViewFormat(IResourceList resourceList) {
        List<Map<String, Object>> returnMap = new LinkedList<>();
        for (IResource resource : resourceList) {
            returnMap.add(IResource.getTableViewFormat(resource));
        }
        return returnMap;
    }

    @Override
    Iterator<IResource> iterator();

    /**
     * Clone a mutable resource list map.
     *
     * @return the mutable resource list map
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    IResourceList create();

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

    IResource get(ResourceType resource);

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
