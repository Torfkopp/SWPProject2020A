package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The interface Development card.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public interface IDevelopmentCard extends Serializable {

    /**
     * Gets the table view format.
     *
     * @param developmentCard The development card
     *
     * @return The table view format
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    static Map<String, Object> getTableViewFormat(IDevelopmentCard developmentCard) {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("card", developmentCard.getType().toString());
        returnMap.put("amount", developmentCard.getAmount());
        return returnMap;
    }

    /**
     * Decrease the amount.
     *
     * @param amount The amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void decrease(int amount);

    /**
     * Decrease the amount.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void decrease();

    /**
     * Gets the amount.
     *
     * @return The amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    int getAmount();

    /**
     * Sets The amount.
     *
     * @param amount The amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void setAmount(int amount);

    /**
     * Gets the type.
     *
     * @return The type
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    DevelopmentCardType getType();

    /**
     * Increase the amount.
     *
     * @param amount The amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void increase(int amount);

    /**
     * Increase the amount.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    void increase();
}
