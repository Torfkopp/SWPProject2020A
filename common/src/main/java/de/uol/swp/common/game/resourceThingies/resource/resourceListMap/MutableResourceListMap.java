package de.uol.swp.common.game.resourceThingies.resource.resourceListMap;

import de.uol.swp.common.game.resourceThingies.resource.ResourceType;
import de.uol.swp.common.game.resourceThingies.resource.resource.*;

import java.util.*;

/**
 * The type Mutable resource list map.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public class MutableResourceListMap implements IMutableResourceListMap {

    private final List<IMutableResource> list;

    /**
     * Instantiates a new Mutable resource list map.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public MutableResourceListMap() {
        list = new LinkedList<>();
        for (ResourceType resource : ResourceType.values()) {
            list.add(new MutableResource(resource, 0));
        }
    }

    /**
     * Instantiates a new Mutable resource list map.
     *
     * @param list the list
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    private MutableResourceListMap(List<IImmutableResource> list) {
        this.list = new LinkedList<>();
        for (IImmutableResource resource : list)
            this.list.add(new MutableResource(resource.getType(), resource.getAmount()));
    }

    /**
     * Create resource list map from a list of resources.
     *
     * @param list the list
     *
     * @return the list of resources
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public static MutableResourceListMap createResourceListMapFromList(List<IImmutableResource> list) {
        return new MutableResourceListMap(list);
    }

    @Override
    public Iterator<IMutableResource> iterator() {
        return new ResourceListMapIterator(list);
    }

    @Override
    public MutableResourceListMap create() {
        return new MutableResourceListMap(list);
    }

    @Override
    public void decrease(ResourceType resource, int amount) {
        increase(resource, -amount);
    }

    @Override
    public void decrease(ResourceType resource) {
        decrease(resource, 1);
    }

    @Override
    public void increase(ResourceType resource, int amount) {
        for (IMutableResource resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) resource1.increase(amount);
        }
    }

    @Override
    public void increase(ResourceType resource) {
        increase(resource, 1);
    }

    @Override
    public void set(ResourceType resource, int amount) {
        for (IMutableResource resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) resource1.setAmount(amount);
        }
    }

    @Override
    public IMutableResource get(ResourceType resource) {
        for (IMutableResource resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) return resource1.create();
        }
        return null;
    }

    @Override
    public int getAmount(ResourceType resource) {
        for (IMutableResource resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) return resource1.getAmount();
        }
        return 0;
    }

    /**
     * The type Resource list map iterator.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public class ResourceListMapIterator implements Iterator<IMutableResource> {

        private final List<IMutableResource> list;

        /**
         * Instantiates a new Resource list map iterator.
         *
         * @param list the list
         *
         * @author Temmo Junkhoff
         * @since 2021-04-23
         */
        public ResourceListMapIterator(List<IMutableResource> list) {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return (!(list.isEmpty()));
        }

        @Override
        public IMutableResource next() {
            return list.remove(0);
        }
    }
}
