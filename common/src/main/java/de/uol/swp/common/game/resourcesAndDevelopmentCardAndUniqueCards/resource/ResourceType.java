package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource;

import com.google.inject.Inject;

import java.io.Serializable;
import java.util.ResourceBundle;

/**
 * The enum for the type of a resource.
 *
 * @author Temmo Junkhoff
 */
public enum ResourceType implements Serializable {

    BRICK("game.resources.brick"),
    GRAIN("game.resources.grain"),
    LUMBER("game.resources.lumber"),
    ORE("game.resources.ore"),
    WOOL("game.resources.wool");

    @Inject
    private static ResourceBundle resourceBundle;

    private final String internationalizationPropertyName;

    /**
     * Constructor.
     *
     * @param internationalizationPropertyName The internationalization property name
     *
     * @author Temmo Junkhoff
     */
    ResourceType(String internationalizationPropertyName) {
        this.internationalizationPropertyName = internationalizationPropertyName;
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
        return internationalizationPropertyName;
    }
}
