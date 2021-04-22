package de.uol.swp.common.game.resourceThingies.resource.resourceListMap;

import de.uol.swp.common.game.resourceThingies.resource.ResourceType;
import de.uol.swp.common.game.resourceThingies.resource.resource.IMutableResource;
import de.uol.swp.common.game.resourceThingies.resource.resource.MutableResource;

import java.util.*;

public class MutableResourceListMap implements IMutableResourceListMap {

    private final List<IMutableResource> list;

    public MutableResourceListMap() {
        list = new LinkedList<>();
        for (ResourceType resource : ResourceType.values()) {
            list.add(new MutableResource(resource, 0));
        }
    }

    private MutableResourceListMap(List<IMutableResource> list) {
        this.list = list;
    }

    public static MutableResourceListMap createResourceListMapFromList(List<IMutableResource> list) {
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

    public class ResourceListMapIterator implements Iterator<IMutableResource> {

        private final List<IMutableResource> list;

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
