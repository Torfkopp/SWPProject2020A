package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard;

import java.io.Serializable;
import java.util.Iterator;

/**
 * The interface Development card list.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public interface IDevelopmentCardList extends Iterable<DevelopmentCard>, Serializable {

    boolean isEmpty();

    @Override
    Iterator<DevelopmentCard> iterator();

    /**
     * Create development card list.
     *
     * @return The development card list
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    DevelopmentCardList create();

    /**
     * Decrease amount of a DevelopmentCard in the List.
     *
     * @param resource The resource
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void decrease(DevelopmentCardType resource);

    /**
     * Get development card.
     *
     * @param resource The resource
     *
     * @return The development card
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    DevelopmentCard get(DevelopmentCardType resource);

    /**
     * Gets the amount.
     *
     * @param resource The resource
     *
     * @return The amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    int getAmount(DevelopmentCardType resource);

    /**
     * Increase amount of a DevelopmentCard in the List.
     *
     * @param resource The resource
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void increase(DevelopmentCardType resource);

    /**
     * Increase amount of a DevelopmentCard in the List by i.
     *
     * @param resource The resource type
     * @param i        The amount to increase
     *
     * @author Marvin Drees
     * @since 2021-05-12
     */
    void increase(DevelopmentCardType resource, int i);

    /**
     * Set the amount for the specified development card type.
     *
     * @param developmentCardType The development card type
     * @param amount              The amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void set(DevelopmentCardType developmentCardType, int amount);
}
