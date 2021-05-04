package de.uol.swp.common.game.map.hexes;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;

/**
 * Class for the resource hex
 *
 * @author Mario Fokken
 * @author Steven Luong
 * @since 2021-01-16
 */
public class ResourceHex extends AbstractHex implements IResourceHex {

    private final ResourceType resource;
    private final int token;

    /**
     * Constructor
     *
     * @param resource The hex's resource
     * @param token    The hex's token
     */
    public ResourceHex(ResourceType resource, int token) {
        this.resource = resource;
        this.token = token;
        setRobberOnField(false);
    }

    @Override
    public ResourceType getResource() {
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
