package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.uniqueCards;

import de.uol.swp.common.user.UserOrDummy;

import java.io.Serializable;

/**
 * The interface Mutable unique card.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public interface IUniqueCard extends Serializable {

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
     * Sets amount.
     *
     * @param amount the amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void setAmount(int amount);

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
     * Sets owner.
     *
     * @param owner the owner
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void setOwner(UserOrDummy owner);

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
