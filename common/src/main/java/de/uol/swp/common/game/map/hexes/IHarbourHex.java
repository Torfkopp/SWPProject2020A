package de.uol.swp.common.game.map.hexes;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;

/**
 * Interface for a harbour hex
 *
 * @author Mario Fokken
 * @author Steven Luong
 * @since 2021-01-16
 */
public interface IHarbourHex extends IWaterHex {

    /**
     * Enum for the five resources a harbour can trade
     * and 'any' if every resource is tradeable
     */
    enum HarbourResource {
        BRICK,
        LUMBER,
        ORE,
        GRAIN,
        WOOL,
        ANY
    }

    /**
     * Enum for the sides the harbour can face
     */
    enum HarbourSide {
        WEST,
        NORTHWEST,
        NORTHEAST,
        EAST,
        SOUTHEAST,
        SOUTHWEST,
    }

    static HarbourResource getHarbourResource(ResourceType resource) {
        switch (resource) {
            case LUMBER:
                return HarbourResource.LUMBER;
            case BRICK:
                return HarbourResource.BRICK;
            case ORE:
                return HarbourResource.ORE;
            case GRAIN:
                return HarbourResource.GRAIN;
            case WOOL:
                return HarbourResource.WOOL;
        }
        return null;
    }

    /**
     * Gets the hex the harbour is belonging to
     *
     * @return The belonging hex
     *
     * @author Maximilian Lindner
     * @author Steven Luong
     * @since 2021-04-07
     */
    GameHexWrapper getBelongingHex();

    /**
     * Gets the harbour's resource
     *
     * @return Resource
     */
    HarbourResource getResource();

    /**
     * Gets the side to which the harbour faces
     *
     * @return Side
     */
    HarbourSide getSide();
}
