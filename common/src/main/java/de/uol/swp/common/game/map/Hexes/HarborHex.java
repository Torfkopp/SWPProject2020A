package de.uol.swp.common.game.map.Hexes;

/**
 * Class for the harbor hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public class HarborHex extends AbstractHex implements IHarborHex {

    private final int belongingHex, side;
    private final resource resource;

    public HarborHex(int belongingHex, int side, resource resource) {
        this.belongingHex = belongingHex;
        this.side = side;
        this.resource = resource;
    }

    @Override
    public int getSide() { return side;}

    @Override
    public resource getResource() {
        return resource;
    }

    @Override
    public type getType() {
        return type.Harbor;
    }

}
