package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.uniqueCards;

import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.common.util.ResourceManager;

/**
 * A class to store a unique card.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public class UniqueCard implements IUniqueCard {

    private final UniqueCardsType type;
    private UserOrDummy owner;
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
    public UniqueCard(UniqueCardsType type, UserOrDummy owner, int amount) {
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
    public UserOrDummy getOwner() {
        return owner;
    }

    @Override
    public void setOwner(UserOrDummy owner) {
        this.owner = owner;
    }

    @Override
    public UniqueCardsType getType() {
        return type;
    }

    @Override
    public String toString() {
        String displayOwner = owner == null ? "nobody" : owner.getUsername();
        if (type == UniqueCardsType.LARGEST_ARMY) {
            return ResourceManager
                    .getIfAvailableElse("Largest Army: %s (%s Knights)", "game.resources.whohas.largestarmy",
                                        displayOwner, amount);
        } else if (type == UniqueCardsType.LONGEST_ROAD) {
            return ResourceManager
                    .getIfAvailableElse("Longest Road: %s (%s Roads)", "game.resources.whohas.longestroad",
                                        displayOwner, amount);
        }
        return type.name() + ": " + displayOwner + ": " + amount;
    }
}
