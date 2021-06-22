package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;

import java.util.HashMap;
import java.util.Map;

/**
 * The player's inventory
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public class Inventory extends AbstractInventory {

    private final Map<DevelopmentCardType, Boolean> isPlayable;
    private int knights = 0;

    /**
     * Constructor for Inventory.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public Inventory() {
        resources = new ResourceList();
        developmentCards = new DevelopmentCardList();
        isPlayable = new HashMap<>();
        for (var type : DevelopmentCardType.values()) {
            isPlayable.put(type, false);
        }
    }

    /**
     * Decrease knights.
     *
     * @param amount The amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void decreaseKnights(int amount) {
        increaseKnights(-amount);
    }

    /**
     * Decrease knights.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void decreaseKnights() {
        decreaseKnights(1);
    }

    /**
     * Gets the amount of Development Cards the user has in his inventory
     *
     * @return The amount of Development Cards
     *
     * @author Alwin Bossert
     * @author Eric Vuong
     * @since 2021-03-27
     */
    public int getAmountOfDevelopmentCards() {
        int returnValue = 0;
        for (DevelopmentCardType developmentCard : DevelopmentCardType.values())
            returnValue += get(developmentCard);
        return returnValue;
    }

    /**
     * Gets the knights.
     *
     * @return The knights
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public int getKnights() {
        return knights;
    }

    /**
     * Gets the amount of Resource Cards (Bricks, Grain, Lumber, Ore, Wool)
     * the user has in his inventory
     *
     * @return The amount of building resources
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @since 2021-02-24
     */
    public int getResourceAmount() {
        int returnValue = 0;
        for (ResourceType resource : ResourceType.values())
            returnValue += get(resource);
        return returnValue;
    }

    /**
     * Increase knights.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void increaseKnights() {
        increaseKnights(1);
    }

    /**
     * Increase knights.
     *
     * @param amount The amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public void increaseKnights(int amount) {
        knights += amount;
    }

    /**
     * Tests if the specified development card type can be played this turn.
     *
     * @param developmentCardType The type of development card for which should be checked if it can be played.
     *
     * @return true if the type of development card is playable, false otherwise.
     *
     * @author Temmo Junkhoff
     * @since 2021-06-22
     */
    public boolean isPlayable(DevelopmentCardType developmentCardType) {
        return isPlayable.get(developmentCardType) && developmentCards.getAmount(developmentCardType) >= 1;
    }

    /**
     * Used to signal the inventory that the turn ended and newly acquired development cards can be played now
     *
     * @author Temmo Junkhoff
     * @since 2021-06-22
     */
    public void nextTurn() {
        for (var x : developmentCards) {
            if (x.getAmount() >= 1) {
                isPlayable.put(x.getType(), true);
            } else {
                isPlayable.put(x.getType(), false);
            }
        }
    }
}
