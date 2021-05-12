package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.IDevelopmentCardList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;

import java.io.Serializable;

/**
 * An abstract class for all inventories.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public abstract class AbstractInventory implements Serializable {

    protected ResourceList resources;
    protected IDevelopmentCardList developmentCards;

    /**
     * Decrease a resource by the given amount.
     *
     * @param resource The resource
     * @param i        The amount to decrease the resource in the inventory by
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void decrease(ResourceType resource, int i) {
        increase(resource, -i);
    }

    /**
     * Decrease the amount of the development card.
     *
     * @param developmentCard The development card
     * @param i               The amount to decrease the DevelopmentCard in the inventory by
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void decrease(DevelopmentCardType developmentCard, int i) {
        increase(developmentCard, -i);
    }

    /**
     * Decrease the amount of the resource.
     *
     * @param resource The resource
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void decrease(ResourceType resource) {
        increase(resource, -1);
    }

    /**
     * Decrease the amount of the development card.
     *
     * @param developmentCard The development card
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void decrease(DevelopmentCardType developmentCard) {
        increase(developmentCard, -1);
    }

    /**
     * Gets the amount of a resource that is in the inventory
     *
     * @return The amount of the resource
     *
     * @since 2021-04-23
     */
    public int get(ResourceType resource) {
        return resources.getAmount(resource);
    }

    /**
     * Gets the amount of Knight Cards that are in the inventory
     *
     * @return The amount of Knight Cards
     *
     * @since 2021-04-23
     */
    public int get(DevelopmentCardType developmentCard) {
        return developmentCards.getAmount(developmentCard);
    }

    /**
     * Gets the development cards in the inventory.
     *
     * @return The development cards
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public DevelopmentCardList getDevelopmentCards() {
        return developmentCards.create();
    }

    /**
     * Gets the resources in the inventory
     *
     * @return The resources
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public ResourceList getResources() {
        return resources.create();
    }

    /**
     * Increase the amount of the resource.
     *
     * @param resource The resource
     * @param i        The amount to increase the resource in the inventory by
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void increase(ResourceType resource, int i) {
        resources.increase(resource, i);
    }

    /**
     * Increase the amount of the development card.
     *
     * @param developmentCard The development card
     * @param i               The
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void increase(DevelopmentCardType developmentCard, int i) {
        developmentCards.get(developmentCard).increase(i);
    }

    /**
     * Increase the amount of the resource.
     *
     * @param resource The resource
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void increase(ResourceType resource) {
        increase(resource, 1);
    }

    /**
     * Increase the amount of the development card.
     *
     * @param developmentCard The development card
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void increase(DevelopmentCardType developmentCard) {
        increase(developmentCard, 1);
    }

    /**
     * Set the resource amount.
     *
     * @param resource The resource
     * @param amount   The amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void set(ResourceType resource, int amount) {
        resources.set(resource, amount);
    }

    /**
     * Set the development card amount.
     *
     * @param developmentCardType The development card type
     * @param amount              The amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void set(DevelopmentCardType developmentCardType, int amount) {
        developmentCards.set(developmentCardType, amount);
    }
}
