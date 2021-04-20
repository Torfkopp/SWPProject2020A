package de.uol.swp.common.game;

import java.util.*;

public class DevelopmentCardListMap implements Iterable<DevelopmentCard> {

    private final List<DevelopmentCard> list;

    public DevelopmentCardListMap() {
        list = new LinkedList<>();
        for (DevelopmentCard.DevelopmentCardType resource : DevelopmentCard.DevelopmentCardType.values()) {
            list.add(new DevelopmentCard(resource, 0));
        }
    }

    private DevelopmentCardListMap(List<DevelopmentCard> list) {
        this.list = list;
    }

    public static DevelopmentCardListMap createDevelopmentCardListMapFromList(List<DevelopmentCard> list){
        return new DevelopmentCardListMap(list);
    }

    @Override
    public Iterator<DevelopmentCard> iterator() {
        return new ResourceListMapIterator(list);
    }

    public DevelopmentCardListMap create() {
        return new DevelopmentCardListMap(list);
    }

    public DevelopmentCard get(DevelopmentCard.DevelopmentCardType resource) {
        for (DevelopmentCard resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) return resource1;
        }
        return null;
    }

    public int getAmount(DevelopmentCard.DevelopmentCardType resource) {
        for (DevelopmentCard resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) return resource1.getAmount();
        }
        return 0;
    }

    public class ResourceListMapIterator implements Iterator<DevelopmentCard> {

        private final List<DevelopmentCard> list;

        public ResourceListMapIterator(List<DevelopmentCard> list) {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return (!(list.isEmpty()));
        }

        @Override
        public DevelopmentCard next() {
            return list.remove(0);
        }
    }
}
