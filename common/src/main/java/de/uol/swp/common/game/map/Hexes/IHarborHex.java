package de.uol.swp.common.game.map.Hexes;

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
        ANY,
        NONE
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
}
