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
    enum resource {
        Brick, Lumber, Ore, Grain, Wool, Any
    }

    /**
     * Gets the harbor's resource
     *
     * @return Resource
     */
    resource getResource();

    /**
     * Gets the side to which the harbor faces
     *
     * @return Side
     */
    int getSide();
}
