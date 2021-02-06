package de.uol.swp.common.game.map.Hexes;

/**
 * Class for the harbor hex
 *
 * @author Mario Fokken
 * @author Steven Luong
 * @since 2021-01-16
 */
public class HarborHex extends AbstractHex implements IHarborHex {

    private final int belongingHex;
    private final HarborSide side;
    private final HarborResource resource;

    /**
     * Constructor
     *
     * @param belongingHex The hex the harbor belongs to
     * @param side         The side the harbor faces
     * @param resource     The resource that is tradeable at the harbor
     */
    public HarborHex(int belongingHex, HarborSide side, HarborResource resource) {
        this.belongingHex = belongingHex;
        this.side = side;
        this.resource = resource;
    }

    @Override
    public HarborResource getResource() {
        return resource;
    }

    @Override
    public HarborSide getSide() { return side;}

    @Override
    public HexType getType() {
        return HexType.HARBOR;
    }
}
