package de.uol.swp.server.game.map;

/**
 * Class for the desert hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public class DesertHex implements IGameMapManagement.IGameHex.ILandHex {

    public DesertHex() {
    }

    @Override
    public type getType() {
        return type.Desert;
    }

}
