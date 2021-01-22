package de.uol.swp.common.game.map.Hexes;

/**
 * Class for the water hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public class WaterHex implements IWaterHex {

    public WaterHex() {
    }

    @Override
    public type getType() {
        return type.Water;
    }

}
