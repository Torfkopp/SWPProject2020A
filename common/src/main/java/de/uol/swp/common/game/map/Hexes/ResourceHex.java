package de.uol.swp.common.game.map.Hexes;

import de.uol.swp.common.game.Resource;

/**
 * Class for the resource hex
 *
 * @author Mario Fokken
 * @author Steven Luong
 * @since 2021-01-16
 */
public class ResourceHex extends AbstractHex implements IResourceHex {

    private final Resource.ResourceType resource;
    private final int token;

    /**
     * Constructor
     *
     * @param resource The hex's resource
     * @param token    The hex's token
     */
    public ResourceHex(Resource.ResourceType resource, int token) {
        this.resource = resource;
        this.token = token;
        setRobberOnField(false);
    }

    @Override
    public Resource.ResourceType getResource() {
        return resource;
    }

    @Override
    public int getToken() {
        return token;
    }

    @Override
    public HexType getType() {
        return HexType.RESOURCE;
    }
}
