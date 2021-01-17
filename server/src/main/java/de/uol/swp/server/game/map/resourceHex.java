package de.uol.swp.server.game.map;

/**
 * Class for the resource hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public class resourceHex implements iGameMapManagement.iGameHex.iResourceHex {
    private resource resource;
    private int token;

    /**
     * Constructor
     *
     * @param resource The hex's resource
     * @param token    The hex's token
     */
    public resourceHex(resource resource, int token) {
        this.resource = resource;
        this.token = token;
    }

    @Override
    public int getToken() {
        return token;
    }

    @Override
    public resource getResource() {
        return resource;
    }

    @Override
    public iGameMapManagement.iGameHex.type type() {
        return iGameMapManagement.iGameHex.type.Resource;
    }
}
