package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ResourceBundle;

/**
 * The enum Development card type.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public enum DevelopmentCardType implements Serializable {

    KNIGHT_CARD("game.resources.cards.knight"),
    ROAD_BUILDING_CARD("game.resources.cards.roadbuilding"),
    YEAR_OF_PLENTY_CARD("game.resources.cards.yearofplenty"),
    MONOPOLY_CARD("game.resources.cards.monopoly"),
    VICTORY_POINT_CARD("game.resources.cards.victorypoints");

    @Inject
    private static ResourceBundle resourceBundle;

    private final String attribute;

    /**
     * Constructor for Development card type.
     *
     * @param attribute The L10n property name
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    DevelopmentCardType(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public String toString() {
        return resourceBundle.getString(getAttributeName());
    }

    /**
     * Gets the attribute name.
     *
     * @return The attribute name
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public String getAttributeName() {
        return attribute;
    }
}
