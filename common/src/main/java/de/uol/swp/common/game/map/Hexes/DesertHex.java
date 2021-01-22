package de.uol.swp.common.game.map.Hexes;

/**
 * Class for the desert hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public class DesertHex implements ILandHex {

    public DesertHex() {
    }

    @Override
    public type getType() {
        return type.Desert;
    }

    @Override
    public void render(int x, int y, int size){}
}
