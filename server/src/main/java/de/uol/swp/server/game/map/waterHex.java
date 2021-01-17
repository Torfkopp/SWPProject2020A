package de.uol.swp.server.game.map;

/**
 * Class for the water hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public class waterHex implements iGameMapManagement.iGameHex.iWaterHex {

    public waterHex() {
    }

    @Override
    public type type() {
        return type.Water;
    }
}
