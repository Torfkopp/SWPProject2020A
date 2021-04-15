package de.uol.swp.common.game;

public enum DevelopmentCard {
    KNIGHT_CARD("game.resources.cards.knight"),
    ROAD_BUILDING_CARD("game.resources.cards.roadbuilding"),
    YEAR_OF_PLENTY_CARD("game.resources.cards.yearofplenty"),
    MONOPOLY_CARD("game.resources.cards.monopoly"),
    VICTORY_POINT_CARD("game.resources.cards.victorypoints");

    private String attribute;

    DevelopmentCard(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }
}
