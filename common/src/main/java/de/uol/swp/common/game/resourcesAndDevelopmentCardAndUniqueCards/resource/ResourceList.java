package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource;

import java.util.*;

/**
 * A class to store a list of resources.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public class ResourceList implements IResourceList {

    private final List<IResource> list;

    /**
     * Constructor.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public ResourceList() {
        list = new LinkedList<>();
        for (ResourceType resource : ResourceType.values()) {
            list.add(new Resource(resource, 0));
        }
    }

    /**
     * Private Constructor.
     * This constructor is exposed through the static method createResourceListMapFromList
     * and is otherwise only needed internally.
     *
     * @param list the list
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    private ResourceList(List<IResource> list) {
        this.list = new LinkedList<>();
        for (IResource resource : list)
            this.list.add(new Resource(resource.getType(), resource.getAmount()));
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
    public static ResourceList createResourceListFromList(List<IResource> list) {
        return new ResourceList(list);
    }

    @Override
    public Iterator<IResource> iterator() {
        return new ResourceListIterator(new LinkedList<>(list));
    }

    @Override
    public ResourceList create() {
        return new ResourceList(list);
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
    public IResource get(ResourceType resource) {
        for (IResource resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) return resource1.create();
        }
        return null;
    }

    @Override
    public int getAmount(ResourceType resource) {
        for (IResource resource1 : list) {
            if (resource == resource1.getType()) {
                return resource1.getAmount();
            }
        }
        return 0;
    }

    @Override
    public void increase(ResourceType resource, int amount) {
        for (IResource resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) resource1.increase(amount);
        }
    }

    @Override
    public void increase(ResourceType resource) {
        increase(resource, 1);
    }

    @Override
    public void set(ResourceType resource, int amount) {
        for (IResource resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) resource1.setAmount(amount);
        }
    }

    /**
     * The type Resource list map iterator.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public class ResourceListIterator implements Iterator<IResource> {

        private final List<IResource> list;

        /**
         * Instantiates a new Resource list map iterator.
         *
         * @param list the list
         *
         * @author Temmo Junkhoff
         * @since 2021-04-23
         */
        public ResourceListIterator(List<IResource> list) {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return (!(list.isEmpty()));
        }

        @Override
        public IResource next() {
            return list.remove(0);
        }
    }
}
