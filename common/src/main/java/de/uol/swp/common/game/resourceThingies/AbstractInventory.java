package de.uol.swp.common.game.resourceThingies;

import de.uol.swp.common.game.resourceThingies.developmentCard.DevelopmentCard;
import de.uol.swp.common.game.resourceThingies.developmentCard.DevelopmentCardListMap;
import de.uol.swp.common.game.resourceThingies.resource.resourceListMap.MutableResourceListMap;
import de.uol.swp.common.game.resourceThingies.resource.ResourceType;

import java.io.Serializable;

public abstract class AbstractInventory implements Serializable {

    protected MutableResourceListMap resources;
    protected DevelopmentCardListMap developmentCards;

    public void decrease(ResourceType resource, int i) {
        increase(resource, -i);
    }

    public void decrease(DevelopmentCard.DevelopmentCardType developmentCard, int i) {
        increase(developmentCard, -i);
    }

    public void decrease(ResourceType resource) {
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
    public int get(ResourceType resource) {
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

    public MutableResourceListMap getResources() {
        return resources.create();
    }

    public void increase(ResourceType resource, int i) {
        resources.get(resource).increase(i);
    }

    public void increase(DevelopmentCard.DevelopmentCardType developmentCard, int i) {
        developmentCards.get(developmentCard).increase(i);
    }

    public void increase(ResourceType resource) {
        increase(resource, 1);
    }

    public void increase(DevelopmentCard.DevelopmentCardType developmentCard) {
        increase(developmentCard, 1);
    }
}
