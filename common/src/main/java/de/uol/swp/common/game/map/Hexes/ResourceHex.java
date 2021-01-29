package de.uol.swp.common.game.map.Hexes;

/**
 * Class for the resource hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public class ResourceHex extends AbstractLandHex implements IResourceHex {
    private final resource resource;
    private final int token;

    /**
     * Constructor
     *
     * @param resource The hex's resource
     * @param token    The hex's token
     */
    public ResourceHex(resource resource, int token) {
        this.resource = resource;
        this.token = token;
        setRobberOnField(false);
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
    public IGameHex.type getType() {
        return IGameHex.type.Resource;
    }

}
