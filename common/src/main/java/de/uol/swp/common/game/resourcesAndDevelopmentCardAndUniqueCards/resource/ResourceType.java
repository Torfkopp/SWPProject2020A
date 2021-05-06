package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ResourceBundle;

/**
 * The enum for the type of a resource.
 *
 * @author Temmo Junkhoff
 */
public enum ResourceType implements Serializable {

    LUMBER("game.resources.lumber"),
    BRICK("game.resources.brick"),
    ORE("game.resources.ore"),
    GRAIN("game.resources.grain"),
    WOOL("game.resources.wool");

    @Inject
    private static ResourceBundle resourceBundle;

    private final String InternationalizationPropertyName;

    /**
     * Constructor.
     *
     * @param InternationalizationPropertyName The internationalization property name
     *
     * @author Temmo Junkhoff
     */
    ResourceType(String InternationalizationPropertyName) {
        this.InternationalizationPropertyName = InternationalizationPropertyName;
    }

    @Override
    public String toString() {
        try {
            return resourceBundle.getString(getInternationalizationPropertyName());
        } catch (NullPointerException ignored) {}
        return "Something went wrong";
    }

    /**
     * Gets the internationalization property name.
     *
     * @return The internationalization property name
     *
     * @author Temmo Junkhoff
     */
    public String getInternationalizationPropertyName() {
        return InternationalizationPropertyName;
    }
}
