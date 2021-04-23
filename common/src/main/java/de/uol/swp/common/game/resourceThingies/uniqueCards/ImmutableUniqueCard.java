package de.uol.swp.common.game.resourceThingies.uniqueCards;

import de.uol.swp.common.user.UserOrDummy;

/**
 * The type Immutable unique card.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public class ImmutableUniqueCard implements IImmutableUniqueCard {

    private final UniqueCardsType type;
    private final UserOrDummy owner;
    private final int amount;

    /**
     * Instantiates a new Immutable unique card.
     *
     * @param type   the type
     * @param owner  the owner
     * @param amount the amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public ImmutableUniqueCard(UniqueCardsType type, UserOrDummy owner, int amount) {
        this.type = type;
        this.owner = owner;
        this.amount = amount;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public UserOrDummy getOwner() {
        return owner;
    }

    @Override
    public UniqueCardsType getType() {
        return type;
    }
}
