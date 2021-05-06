package de.uol.swp.common.game.map.hexes;

/**
 * Class for the harbor hex
 *
 * @author Mario Fokken
 * @author Steven Luong
 * @since 2021-01-16
 */
public class HarborHex extends AbstractHex implements IHarborHex {

    private final GameHexWrapper belongingHex;
    private final HarborSide side;
    private final HarborResource resource;

    /**
     * Constructor
     *
     * @param belongingHex The hex the harbor belongs to
     * @param side         The side the harbor faces
     * @param resource     The resource that is tradeable at the harbor
     */
    public HarborHex(GameHexWrapper belongingHex, HarborSide side, HarborResource resource) {
        this.belongingHex = belongingHex;
        this.side = side;
        this.resource = resource;
    }

    @Override
    public GameHexWrapper getBelongingHex() {
        return belongingHex;
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
