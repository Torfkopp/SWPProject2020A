package de.uol.swp.common.game;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ResourceBundle;

public class DevelopmentCard implements Serializable {

    @Inject
    private static ResourceBundle resourceBundle;

    private final DevelopmentCardType type;
    private int amount;

    public enum DevelopmentCardType {
        KNIGHT_CARD("game.resources.cards.knight"),
        ROAD_BUILDING_CARD("game.resources.cards.roadbuilding"),
        YEAR_OF_PLENTY_CARD("game.resources.cards.yearofplenty"),
        MONOPOLY_CARD("game.resources.cards.monopoly"),
        VICTORY_POINT_CARD("game.resources.cards.victorypoints");

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

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public DevelopmentCard(DevelopmentCardType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public void decrease(int amount) {
        increase(-amount);
    }

    public void decrease() {
        decrease(-1);
    }

    public int getAmount() {
        return amount;
    }

    public DevelopmentCardType getType() {
        return type;
    }

    public void increase(int amount) {
        this.amount += amount;
    }

    public void increase() {
        increase(1);
    }
}
