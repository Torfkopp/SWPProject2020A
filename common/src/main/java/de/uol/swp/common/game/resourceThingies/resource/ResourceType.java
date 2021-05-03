package de.uol.swp.common.game.resourceThingies.resource;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ResourceBundle;

public enum ResourceType implements Serializable {

    LUMBER("game.resources.lumber"),
    BRICK("game.resources.brick"),
    ORE("game.resources.ore"),
    GRAIN("game.resources.grain"),
    WOOL("game.resources.wool");

    @Inject
    private static ResourceBundle resourceBundle;

    private final String attributeName;

    ResourceType(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public String toString() {
        if (resourceBundle == null) return "";
        String string = resourceBundle.getString(getAttributeName());
        return string == null ? "" : string;
    }

    public String getAttributeName() {
        return attributeName;
    }
}
