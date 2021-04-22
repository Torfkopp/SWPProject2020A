package de.uol.swp.common.game.resourceThingies.resource.resourceListMap;

import de.uol.swp.common.game.resourceThingies.resource.ResourceType;
import de.uol.swp.common.game.resourceThingies.resource.resource.IImmutableResource;
import de.uol.swp.common.game.resourceThingies.resource.resource.ImmutableResource;

import java.util.*;

public class ImmutableResourceListMap implements IImmutableResourceListMap {

    private final List<IImmutableResource> list;

    public ImmutableResourceListMap() {
        list = new LinkedList<>();
        for (ResourceType resource : ResourceType.values()) {
            list.add(new ImmutableResource(resource, 0));
        }
    }

    private ImmutableResourceListMap(List<IImmutableResource> list) {
        this.list = list;
    }

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

    public class ImmutableResourceListMapIterator implements Iterator<IImmutableResource> {

        private final List<IImmutableResource> list;

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
