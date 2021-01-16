package de.uol.swp.server.game;

/**
 *
 */
public interface iHarborHex {

    enum resource{
        Brick, Lumber, Ore, Grain, Wool, Any
    }

    resource getResource();

}
