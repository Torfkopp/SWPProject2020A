package de.uol.swp.common.game.resourceThingies.developmentCard;

import java.util.*;

public class DevelopmentCardList implements IDevelopmentCardList {

    private final List<DevelopmentCard> list;

    public DevelopmentCardList() {
        list = new LinkedList<>();
        for (DevelopmentCardType resource : DevelopmentCardType.values()) {
            list.add(new DevelopmentCard(resource, 0));
        }
    }

    private DevelopmentCardList(List<DevelopmentCard> list) {
        this.list = list;
    }

    public static DevelopmentCardList createDevelopmentCardListFromList(List<DevelopmentCard> list) {
        return new DevelopmentCardList(list);
    }

    @Override
    public Iterator<DevelopmentCard> iterator() {
        return new ResourceListMapIterator(list);
    }

    @Override
    public DevelopmentCardList create() {
        return new DevelopmentCardList(list);
    }

    @Override
    public void decrease(DevelopmentCardType resource) {
        for (DevelopmentCard resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) resource1.decrease();
        }
    }

    @Override
    public DevelopmentCard get(DevelopmentCardType resource) {
        for (DevelopmentCard resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) return resource1;
        }
        return null;
    }

    @Override
    public int getAmount(DevelopmentCardType resource) {
        for (DevelopmentCard resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) return resource1.getAmount();
        }
        return 0;
    }

    @Override
    public void increase(DevelopmentCardType resource) {
        for (DevelopmentCard resource1 : list) {
            if (Objects.equals(resource, resource1.getType())) resource1.increase();
        }
    }

    public void set(DevelopmentCardType developmentCardType, int amount) {
        for (DevelopmentCard developmentCard : list) {
            if (Objects.equals(developmentCardType, developmentCard.getType())) developmentCard.setAmount(amount);
        }
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
