package de.uol.swp.common.game.resourceThingies.resource.resourceListMap;

import de.uol.swp.common.game.resourceThingies.resource.ResourceType;
import de.uol.swp.common.game.resourceThingies.resource.resource.IImmutableResource;
import de.uol.swp.common.game.resourceThingies.resource.resource.ImmutableResource;

import java.util.*;

/**
 * The type Immutable resource list map.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public class ImmutableResourceListMap implements IImmutableResourceListMap {

    private final List<IImmutableResource> list;

    /**
     * Instantiates a new Immutable resource list map.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public ImmutableResourceListMap() {
        list = new LinkedList<>();
        for (ResourceType resource : ResourceType.values()) {
            list.add(new ImmutableResource(resource, 0));
        }
    }

    /**
     * Instantiates a new Immutable resource list map.
     *
     * @param list the list
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    private ImmutableResourceListMap(List<IImmutableResource> list) {
        this.list = list;
    }

    /**
     * Create resource list map from a list of resources.
     *
     * @param list the list
     *
     * @return the immutable resource list map
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public static ImmutableResourceListMap createResourceListMapFromList(List<IImmutableResource> list) {
        return new ImmutableResourceListMap(list);
    }

    @Override
    public Iterator<IImmutableResource> iterator() {
        return new ImmutableResourceListMapIterator(list);
    }

    @Override
    public IImmutableResource get(ResourceType resource) {
        for (IImmutableResource resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) return resource1.getImmutable();
        }
        return null;
    }

    @Override
    public int getAmount(ResourceType resource) {
        for (IImmutableResource resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) return resource1.getAmount();
        }
        return 0;
    }

    /**
     * The type Immutable resource list map iterator.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public class ImmutableResourceListMapIterator implements Iterator<IImmutableResource> {

        private final List<IImmutableResource> list;

        /**
         * Instantiates a new Immutable resource list map iterator.
         *
         * @param list the list
         *
         * @author Temmo Junkhoff
         * @since 2021-04-23
         */
        public ImmutableResourceListMapIterator(List<IImmutableResource> list) {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return (!(list.isEmpty()));
        }

        @Override
        public IImmutableResource next() {
            return list.remove(0);
        }
    }
}
