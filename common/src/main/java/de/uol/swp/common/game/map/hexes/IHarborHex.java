package de.uol.swp.common.game.map.hexes;

import de.uol.swp.common.game.resourceThingies.resource.ResourceType;

/**
 * Interface for a harbor hex
 *
 * @author Mario Fokken
 * @author Steven Luong
 * @since 2021-01-16
 */
public interface IHarborHex extends IWaterHex {

    /**
     * Enum for the five resources a harbor can trade
     * and 'any' if every resource is tradeable
     */
    enum HarborResource {
        BRICK,
        LUMBER,
        ORE,
        GRAIN,
        WOOL,
        ANY
    }

    static HarborResource getHarborResource(ResourceType resource){
        switch (resource){

            case LUMBER:
                return HarborResource.LUMBER;
            case BRICK:
                return HarborResource.BRICK;
            case ORE:
                return HarborResource.ORE;
            case GRAIN:
                return HarborResource.GRAIN;
            case WOOL:
                return HarborResource.WOOL;
        }
        return null;
    }

    /**
     * Enum for the sides the harbor can face
     */
    enum HarborSide {
        WEST,
        NORTHWEST,
        NORTHEAST,
        EAST,
        SOUTHEAST,
        SOUTHWEST,
    }

    /**
     * Gets the hex the harbor is belonging to
     *
     * @return The belonging hex
     *
     * @author Maximilian Lindner
     * @author Steven Luong
     * @since 2021-04-07
     */
    GameHexWrapper getBelongingHex();

    /**
     * Gets the harbor's resource
     *
     * @return Resource
     */
    HarborResource getResource();

    /**
     * Gets the side to which the harbor faces
     *
     * @return Side
     */
    HarborSide getSide();
}
