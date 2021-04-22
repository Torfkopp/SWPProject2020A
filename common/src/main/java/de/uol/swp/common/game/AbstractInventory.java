package de.uol.swp.common.game;

import java.io.Serializable;

public abstract class AbstractInventory implements Serializable {

    protected ResourceListMap resources;
    protected DevelopmentCardListMap developmentCards;

    public void decrease(Resource.ResourceType resource, int i) {
        increase(resource, -i);
    }

    public void decrease(DevelopmentCard.DevelopmentCardType developmentCard, int i) {
        increase(developmentCard, -i);
    }

    public void decrease(Resource.ResourceType resource) {
        increase(resource, -1);
    }

    public void decrease(DevelopmentCard.DevelopmentCardType developmentCard) {
        increase(developmentCard, -1);
    }

    /**
     * Gets the amount of a resource the bank has in the inventory
     *
     * @return The amount of the resource
     */
    public int get(Resource.ResourceType resource) {
        return resources.getAmount(resource);
    }

    /**
     * Gets the amount of Knight Cards a bank has in the inventory
     *
     * @return The amount of Knight Cards
     */
    public int get(DevelopmentCard.DevelopmentCardType developmentCard) {
        return developmentCards.getAmount(developmentCard);
    }

    public DevelopmentCardListMap getDevelopmentCards() {
        return developmentCards.create();
    }

    public ResourceListMap getResources() {
        return resources.create();
    }

    public void increase(Resource.ResourceType resource, int i) {
        resources.get(resource).increase(i);
    }

    public void increase(DevelopmentCard.DevelopmentCardType developmentCard, int i) {
        developmentCards.get(developmentCard).increase(i);
    }

    public void increase(Resource.ResourceType resource) {
        increase(resource, 1);
    }

    public void increase(DevelopmentCard.DevelopmentCardType developmentCard) {
        increase(developmentCard, 1);
    }
}
