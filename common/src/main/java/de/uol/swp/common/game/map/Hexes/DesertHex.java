package de.uol.swp.common.game.map.Hexes;

/**
 * Class for the desert hex
 *
 * @author Mario Fokken
 * @author Steven Luong
 * @since 2021-01-16
 */
public class DesertHex extends AbstractHex implements ILandHex {

    /**
     * Constructor
     */
    public DesertHex() {
        setRobberOnField(false);
    }

    @Override
    public HexType getType() {
        return HexType.DESERT;
    }
}
