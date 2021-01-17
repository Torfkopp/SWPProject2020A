package de.uol.swp.server.game.map;

/**
 * Class for the harbor hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public class harborHex implements iGameMapManagement.iGameHex.iHarborHex {

    private int belongingHex;
    private resource resource;

    public harborHex(int belongingHex, resource resource) {
        this.belongingHex = belongingHex;
        this.resource = resource;
    }

    @Override
    public resource getResource() {
        return resource;
    }

    @Override
    public type type() {
        return type.Harbor;
    }
}
