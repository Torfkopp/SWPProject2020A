package de.uol.swp.common.game.map.hexes;

import java.io.Serializable;

/**
 * A wrapper class for IGameHex
 *
 * @author Marvin Drees
 * @author Temmo Junkhoff
 */
public class GameHexWrapper implements Serializable {

    private IGameHex hex;

    /**
     * Constructor
     */
    public GameHexWrapper() {
        hex = new WaterHex();
    }

    /**
     * Gets the hex stored in the wrapper
     *
     * @return The stored hex
     */
    public IGameHex get() {
        return hex;
    }

    /**
     * Setter for the hex
     *
     * @param hex Hex object to be stored
     */
    public void set(IGameHex hex) {
        this.hex = hex;
    }
}
