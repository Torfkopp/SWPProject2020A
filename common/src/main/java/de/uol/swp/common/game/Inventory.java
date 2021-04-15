package de.uol.swp.common.game;

/**
 * The player's inventory
 *
 * @author Mario Fokken
 * @author Steven Luong
 * @since 2021-01-16
 */
public class Inventory extends AbstractInventory {

    private int knights = 0;

    public Inventory() {
        for (Resource resource : Resource.values()) {
            super.resources.put(resource, 0);
        }
        for (DevelopmentCard developmentCard : DevelopmentCard.values()) {
            super.developmentCards.put(developmentCard, 0);
        }
    }

    public void decreaseKnights(int amount) {
        increaseKnights(-amount);
    }

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
        for (DevelopmentCard developmentCard : DevelopmentCard.values())
            returnValue += get(developmentCard);
        return returnValue;
    }

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
        for (Resource resource : Resource.values())
            returnValue += get(resource);
        return returnValue;
    }

    public void increaseKnights() {
        increaseKnights(1);
    }

    public void increaseKnights(int amount) {
        knights += amount;
    }
}
