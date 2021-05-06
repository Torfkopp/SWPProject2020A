package de.uol.swp.common.game.map.hexes;

/**
 * Class for the water hex
 *
 * @author Mario Fokken
 * @author Steven Luong
 * @since 2021-01-16
 */
public class WaterHex extends AbstractHex implements IWaterHex {

    public WaterHex() {
    }

    @Override
    public HexType getType() {
        return HexType.WATER;
    }
}
