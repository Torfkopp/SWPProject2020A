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
     * Gets the harbor's resource
     *
     * @return Resource
     */
    resource getResource();

    /**
     * Enum for the five resources a harbor can trade
     * and 'any' if every resource is tradeable
     */
    enum resource {
        Brick, Lumber, Ore, Grain, Wool, Any
    }

}
