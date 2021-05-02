package de.uol.swp.common.game.map.hexes;

import de.uol.swp.common.game.resourceThingies.resource.ResourceType;

/**
 * Interface for a resource hex
 *
 * @author Mario Fokken
 * @author Steven Luong
 * @since 2021-01-16
 */
public interface IResourceHex extends ILandHex {

    /**
     * Gets the hex's resource
     *
     * @return Resource
     */
    ResourceType getResource();

    /**
     * Gets the number of the hex's token
     *
     * @return int Token number
     */
    int getToken();
}
