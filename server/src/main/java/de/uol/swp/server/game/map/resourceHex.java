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
    private int[] neighbours;

    /**
     * Constructor
     *
     * @param resource   The hex's resource
     * @param token      The hex's token
     * @param neighbours Array of the six neighbours (clockwise)
     */
    public resourceHex(resource resource, int token, int[] neighbours) {
        this.resource = resource;
        this.token = token;
        this.neighbours = neighbours;
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
    public int[] getNeighbours() {
        return neighbours;
    }

    @Override
    public iGameMapManagement.iGameHex.type type() {
        return iGameMapManagement.iGameHex.type.Resource;
    }
}
