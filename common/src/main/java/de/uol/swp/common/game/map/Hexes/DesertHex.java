package de.uol.swp.common.game.map.Hexes;

/**
 * Class for the desert hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public class DesertHex extends AbstractHex implements ILandHex {

    public DesertHex() {
        setRobberOnField(false);
    }

    @Override
    public type getType() {
        return type.Desert;
    }


}
