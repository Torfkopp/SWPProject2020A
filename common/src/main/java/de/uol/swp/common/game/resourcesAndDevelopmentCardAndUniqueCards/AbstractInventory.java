package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;

import java.io.Serializable;

/**
 * The type Abstract inventory.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public abstract class AbstractInventory implements Serializable {

    protected ResourceList resources;
    protected DevelopmentCardList developmentCards;

    /**
     * Decrease the resource.
     *
     * @param resource The resource
     * @param i        The
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void decrease(ResourceType resource, int i) {
        increase(resource, -i);
    }

    /**
     * Decrease the development card.
     *
     * @param developmentCard The development card
     * @param i               The
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void decrease(DevelopmentCardType developmentCard, int i) {
        increase(developmentCard, -i);
    }

    /**
     * Decrease the resource.
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
     * Decrease the development card.
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
     * Gets the amount of a resource the bank has in the inventory
     *
     * @return The amount of the resource
     *
     * @since 2021-04-23
     */
    public int get(ResourceType resource) {
        return resources.getAmount(resource);
    }

    /**
     * Gets the amount of Knight Cards a bank has in the inventory
     *
     * @return The amount of Knight Cards
     *
     * @since 2021-04-23
     */
    public int get(DevelopmentCardType developmentCard) {
        return developmentCards.getAmount(developmentCard);
    }

    /**
     * Gets the development cards.
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
     * Gets the resources.
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
     * Increase the resource.
     *
     * @param resource The resource
     * @param i        The
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void increase(ResourceType resource, int i) {
        resources.increase(resource, i);
    }

    /**
     * Increase the development card.
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
     * Increase the resource.
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
     * Increase the development card.
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
