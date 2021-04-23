package de.uol.swp.common.game.resourceThingies.uniqueCards;

import de.uol.swp.common.user.UserOrDummy;

/**
 * The interface Mutable unique card.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public interface IMutableUniqueCard extends IImmutableUniqueCard {

    /**
     * Sets amount.
     *
     * @param amount the amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void setAmount(int amount);

    /**
     * Sets owner.
     *
     * @param owner the owner
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void setOwner(UserOrDummy owner);
}
