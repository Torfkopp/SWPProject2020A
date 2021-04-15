package de.uol.swp.common.game;

public enum Resource {
    LUMBER("game.resource.lumber"),
    BRICK("game.resource.brick"),
    ORE("game.resource.ore"),
    GRAIN("game.resource.grain"),
    WOOL("game.resource.wool");

    private final String attributeName;

    Resource(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return attributeName;
    }
}
