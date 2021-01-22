package de.uol.swp.common.game.map.Hexes;

/**
 * Interface for a harbor hex
 *
 * @author Mario
 * @author Steven
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

}
