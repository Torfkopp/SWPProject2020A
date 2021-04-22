package de.uol.swp.common.game;

import java.util.*;

public class ResourceListMap implements Iterable<Resource> {

    private final List<Resource> list;

    public ResourceListMap() {
        list = new LinkedList<>();
        for (Resource.ResourceType resource : Resource.ResourceType.values()) {
            list.add(new Resource(resource, 0));
        }
    }

    private ResourceListMap(List<Resource> list) {
        this.list = list;
    }

    public static ResourceListMap createResourceListMapFromList(List<Resource> list){
        return new ResourceListMap(list);
    }

    @Override
    public Iterator<Resource> iterator() {
        return new ResourceListMapIterator(list);
    }

    public ResourceListMap create() {
        return new ResourceListMap(list);
    }

    public Resource get(Resource.ResourceType resource) {
        for (Resource resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) return resource1.create();
        }
        return null;
    }

    public void increase(Resource.ResourceType resource){
        increase(resource, 1);
    }

    public void increase(Resource.ResourceType resource, int amount){
        for (Resource resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) resource1.increase(amount);
        }
    }

    public void set(Resource.ResourceType resource, int amount){
        for (Resource resource1 : list){
            if (Objects.equals(resource, resource1.getType())) resource1.setAmount(amount);

        }
    }

    public void decrease(Resource.ResourceType resource, int amount){
        increase(resource,-amount);
    }

    public void decrease(Resource.ResourceType resource){
        decrease(resource,1);
    }

    public int getAmount(Resource.ResourceType resource) {
        for (Resource resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) return resource1.getAmount();
        }
        return 0;
    }

    public class ResourceListMapIterator implements Iterator<Resource> {

        private final List<Resource> list;

        public ResourceListMapIterator(List<Resource> list) {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return (!(list.isEmpty()));
        }

        @Override
        public Resource next() {
            return list.remove(0);
        }
    }
}
