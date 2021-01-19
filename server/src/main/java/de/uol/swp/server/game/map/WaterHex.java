package de.uol.swp.server.game.map;

/**
 * Class for the water hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public class WaterHex implements IGameMapManagement.IGameHex.IWaterHex {

    public WaterHex() {
    }

    @Override
    public type getType() {
        return type.Water;
    }
}
