package de.uol.swp.common.game.resourceThingies;

import de.uol.swp.common.game.resourceThingies.developmentCard.DevelopmentCardList;
import de.uol.swp.common.game.resourceThingies.developmentCard.DevelopmentCardType;
import de.uol.swp.common.game.resourceThingies.resource.ResourceList;
import de.uol.swp.common.game.resourceThingies.resource.ResourceType;

import java.io.Serializable;

public abstract class AbstractInventory implements Serializable {

    protected ResourceList resources;
    protected DevelopmentCardList developmentCards;

    public void decrease(ResourceType resource, int i) {
        increase(resource, -i);
    }

    public void decrease(DevelopmentCardType developmentCard, int i) {
        increase(developmentCard, -i);
    }

    public void decrease(ResourceType resource) {
        increase(resource, -1);
    }

    public void decrease(DevelopmentCardType developmentCard) {
        increase(developmentCard, -1);
    }

    /**
     * Gets the amount of a resource the bank has in the inventory
     *
     * @return The amount of the resource
     */
    public int get(ResourceType resource) {
        return resources.getAmount(resource);
    }

    /**
     * Gets the amount of Knight Cards a bank has in the inventory
     *
     * @return The amount of Knight Cards
     */
    public int get(DevelopmentCardType developmentCard) {
        return developmentCards.getAmount(developmentCard);
    }

    public DevelopmentCardList getDevelopmentCards() {
        return developmentCards.create();
    }

    public ResourceList getResources() {
        return resources.create();
    }

    public void increase(ResourceType resource, int i) {
        resources.increase(resource, i);
    }

    public void increase(DevelopmentCardType developmentCard, int i) {
        developmentCards.get(developmentCard).increase(i);
    }

    public void increase(ResourceType resource) {
        increase(resource, 1);
    }

    public void increase(DevelopmentCardType developmentCard) {
        increase(developmentCard, 1);
    }
}
