package de.uol.swp.common.game.resourceThingies.uniqueCards;

import de.uol.swp.common.user.UserOrDummy;

/**
 * The interface Immutable unique card.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public interface IImmutableUniqueCard {

    /**
     * Gets amount.
     *
     * @return the amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    int getAmount();

    /**
     * Gets owner.
     *
     * @return the owner
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    UserOrDummy getOwner();

    /**
     * Gets type.
     *
     * @return the type
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    UniqueCardsType getType();
}
