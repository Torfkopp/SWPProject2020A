package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.uniqueCards;

import de.uol.swp.common.user.Actor;
import de.uol.swp.common.util.ResourceManager;

/**
 * A class to store a unique card.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public class UniqueCard implements IUniqueCard {

    private final UniqueCardsType type;
    private Actor owner;
    private int amount;

    /**
     * Constructor for an empty unique card.
     *
     * @param type the type
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public UniqueCard(UniqueCardsType type) {
        this(type, null, 0);
    }

    /**
     * Constructor.
     *
     * @param type   the type
     * @param owner  the owner
     * @param amount the amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public UniqueCard(UniqueCardsType type, Actor owner, int amount) {
        this.type = type;
        this.owner = owner;
        this.amount = amount;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public Actor getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Actor owner) {
        this.owner = owner;
    }

    @Override
    public UniqueCardsType getType() {
        return type;
    }

    @Override
    public String toString() {
        String displayOwner = owner == null ? "nobody" : owner.getUsername();
        switch (type) {
            case LARGEST_ARMY:
                return ResourceManager
                        .getIfAvailableElse("Largest Army: %s (%s Knights)", "game.resources.whohas.largestarmy",
                                            displayOwner, amount);
            case LONGEST_ROAD:
                return ResourceManager
                        .getIfAvailableElse("Longest Road: %s (%s Roads)", "game.resources.whohas.longestroad",
                                            displayOwner, amount);
            case ARMY_SIZE:
                return ResourceManager.getIfAvailableElse("You have %s Knights", "game.resources.knights", amount);
        }
        return type.name() + ": " + displayOwner + ": " + amount;
    }
}
