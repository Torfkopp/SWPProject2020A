package de.uol.swp.server.game.map.Hexes;

/**
 * Interface for a resource hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public interface IResourceHex extends ILandHex {

    /**
     * Enum for all five resource giving hex types
     */
    enum resource {
        Hills, Forest, Mountains, Fields, Pasture
    }

    /**
     * Gets the number of the hex's token
     *
     * @return int Token number
     */
    int getToken();

    /**
     * Gets the hex's resource
     *
     * @return Resource
     */
    resource getResource();

}
