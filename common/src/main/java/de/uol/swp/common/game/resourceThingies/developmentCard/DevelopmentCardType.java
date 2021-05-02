package de.uol.swp.common.game.resourceThingies.developmentCard;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ResourceBundle;

public enum DevelopmentCardType implements Serializable {

    KNIGHT_CARD("game.resources.cards.knight"),
    ROAD_BUILDING_CARD("game.resources.cards.roadbuilding"),
    YEAR_OF_PLENTY_CARD("game.resources.cards.yearofplenty"),
    MONOPOLY_CARD("game.resources.cards.monopoly"),
    VICTORY_POINT_CARD("game.resources.cards.victorypoints");

    @Inject
    private static ResourceBundle resourceBundle;

    private final String attribute;

    DevelopmentCardType(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public String toString() {
        return resourceBundle.getString(getAttributeName());
    }

    public String getAttributeName() {
        return attribute;
    }
}
