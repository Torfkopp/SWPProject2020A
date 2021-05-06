package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard;

import java.util.*;

/**
 * The class for a list of development cards.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public class DevelopmentCardList implements IDevelopmentCardList {

    private final List<DevelopmentCard> list;

    /**
     * Constructor for Development card list.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public DevelopmentCardList() {
        list = new LinkedList<>();
        for (DevelopmentCardType resource : DevelopmentCardType.values()) {
            list.add(new DevelopmentCard(resource, 0));
        }
    }

    /**
     * Constructor for Development card list.
     * This is constructor is wrapped in a static method for outside access.
     * It is also needed internally to clone the class and create the iterator.
     *
     * @param list The list
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    private DevelopmentCardList(List<DevelopmentCard> list) {
        this.list = list;
    }

    /**
     * Create development card list from list of development card.
     *
     * @param list The list
     *
     * @return The development card list
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
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

    @Override
    public void set(DevelopmentCardType developmentCardType, int amount) {
        for (DevelopmentCard developmentCard : list) {
            if (Objects.equals(developmentCardType, developmentCard.getType())) developmentCard.setAmount(amount);
        }
    }

    /**
     * The type Resource list map iterator.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public class ResourceListMapIterator implements Iterator<DevelopmentCard> {

        private final List<DevelopmentCard> list;

        /**
         * Constructor for Resource list map iterator.
         *
         * @param list The list
         *
         * @author Temmo Junkhoff
         * @since 2021-04-23
         */
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
