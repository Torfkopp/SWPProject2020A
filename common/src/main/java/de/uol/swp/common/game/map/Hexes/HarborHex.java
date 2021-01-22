package de.uol.swp.common.game.map.Hexes;

/**
 * Class for the harbor hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public class HarborHex implements IHarborHex {

    private int belongingHex;
    private resource resource;

    public HarborHex(int belongingHex, resource resource) {
        this.belongingHex = belongingHex;
        this.resource = resource;
    }

    @Override
    public resource getResource() {
        return resource;
    }

    @Override
    public type getType() {
        return type.Harbor;
    }

}
