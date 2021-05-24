package de.uol.swp.common.game.map.hexes;

/**
 * Class for the harbour hex
 *
 * @author Mario Fokken
 * @author Steven Luong
 * @since 2021-01-16
 */
public class HarbourHex extends AbstractHex implements IHarbourHex {

    private final GameHexWrapper belongingHex;
    private final HarbourSide side;
    private final HarbourResource resource;

    /**
     * Constructor
     *
     * @param belongingHex The hex the harbour belongs to
     * @param side         The side the harbour faces
     * @param resource     The resource that is tradeable at the harbour
     */
    public HarbourHex(GameHexWrapper belongingHex, HarbourSide side, HarbourResource resource) {
        this.belongingHex = belongingHex;
        this.side = side;
        this.resource = resource;
    }

    @Override
    public GameHexWrapper getBelongingHex() {
        return belongingHex;
    }

    @Override
    public HarbourResource getResource() {
        return resource;
    }

    @Override
    public HarbourSide getSide() { return side;}

    @Override
    public HexType getType() {
        return HexType.HARBOUR;
    }
}
