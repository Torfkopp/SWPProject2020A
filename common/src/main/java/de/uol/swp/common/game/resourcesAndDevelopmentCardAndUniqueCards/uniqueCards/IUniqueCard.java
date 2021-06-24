package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.uniqueCards;

import de.uol.swp.common.user.Actor;

import java.io.Serializable;

/**
 * An interface to model a unique card.
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
     * Gets owner of this Unique Card.
     *
     * @return the owner
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    Actor getOwner();

    /**
     * Sets owner of this Unique Card.
     *
     * @param owner the owner
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void setOwner(Actor owner);

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
