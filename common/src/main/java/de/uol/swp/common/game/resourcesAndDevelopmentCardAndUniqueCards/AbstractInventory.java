package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards;

import de.uol.swp.common.exception.NotEnoughResourcesException;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.IDevelopmentCardList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.IResource;
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
     * @throws de.uol.swp.common.exception.NotEnoughResourcesException If the decrease would make the Resource amount negative
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void decrease(ResourceType resource, int i) throws NotEnoughResourcesException {
        increase(resource, -i);
    }

    /**
     * Decrease the amount of the development card.
     *
     * @param developmentCard The development card
     * @param i               The amount to decrease the DevelopmentCard in the inventory by
     *
     * @throws de.uol.swp.common.exception.NotEnoughResourcesException If the decrease would make the Development Card amount negative
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void decrease(DevelopmentCardType developmentCard, int i) throws NotEnoughResourcesException {
        increase(developmentCard, -i);
    }

    /**
     * Decrease the amount of the resource.
     *
     * @param resource The resource
     *
     * @throws de.uol.swp.common.exception.NotEnoughResourcesException If the decrease would make the Resource amount negative
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void decrease(ResourceType resource) throws NotEnoughResourcesException {
        increase(resource, -1);
    }

    /**
     * Decrease the amount of the development card.
     *
     * @param developmentCard The development card
     *
     * @throws de.uol.swp.common.exception.NotEnoughResourcesException If the decrease would make the Development Card amount negative
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void decrease(DevelopmentCardType developmentCard) throws NotEnoughResourcesException {
        increase(developmentCard, -1);
    }

    /**
     * Gets the amount of a resource in this inventory
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
     * @throws de.uol.swp.common.exception.NotEnoughResourcesException If the increase would make the Resource amount negative
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void increase(ResourceType resource, int i) throws NotEnoughResourcesException {
        if (resources.get(resource).getAmount() + i < 0)
            throw new NotEnoughResourcesException("Can't have a negative amount of Resources");
        resources.increase(resource, i);
    }

    /**
     * Increase the amount of the development card.
     *
     * @param developmentCard The development card
     * @param i               The
     *
     * @throws de.uol.swp.common.exception.NotEnoughResourcesException If the increase would make the Development Card amount negative
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void increase(DevelopmentCardType developmentCard, int i) throws NotEnoughResourcesException {
        if (developmentCards.get(developmentCard).getAmount() + i < 0)
            throw new NotEnoughResourcesException("Can't have a negative amount of Development Cards");
        developmentCards.increase(developmentCard, i);
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
        try {
            increase(resource, 1);
            // we can ignore this exception because we guarantee an _increase_
        } catch (NotEnoughResourcesException ignored) {}
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
        try {
            increase(developmentCard, 1);
            // we can ignore this exception because we guarantee an _increase_
        } catch (NotEnoughResourcesException ignored) {}
    }

    /**
     * Increases all resources by the given amount
     *
     * @param i The amount to increase the resources in the inventory by
     *
     * @throws de.uol.swp.common.exception.NotEnoughResourcesException If the increase would make the Resource amount negative
     * @author Maximilian Lindner
     * @since 2021 -05-12
     */
    public void increaseAll(int i) throws NotEnoughResourcesException {
        for (IResource resource : resources) {
            increase(resource.getType(), i);
        }
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
