package de.uol.swp.common.game.resourceThingies.uniqueCards;

import de.uol.swp.common.user.UserOrDummy;

/**
 * The type Mutable unique card.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public class UniqueCard implements IUniqueCard {

    private final UniqueCardsType type;
    private UserOrDummy owner;
    private int amount;

    /**
     * Instantiates a new Mutable unique card.
     *
     * @param type the type
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public UniqueCard(UniqueCardsType type) {
        this.type = type;
        this.owner = null;
        this.amount = 0;
    }

    /**
     * Instantiates a new Mutable unique card.
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
}
