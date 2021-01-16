package de.uol.swp.server.game;

/**
 * Interface for a hex
 * @since 2021-01-16
 */
public interface iGameHex {

    enum type{
        Hills, Forest, Mountains, Fields, Pasture, Desert
    }

    /**
     * Gets the number of the hex's token
     * @return int Token number
     */
    int getToken();

    /**
     *  Gets the hex's type
     * @return String Type of hex
     */
    type getType();

}
