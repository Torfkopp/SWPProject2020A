package de.uol.swp.server.game;

/**
 *
 */
public interface iGameMap {

    iGameHex getHex(int place);

    boolean placeSettlement(String colour, int position);

    boolean placeStreet(String colour, int position);

    void moveRobber(int newPosition);

    boolean upgradeSettlement(String colour, int position);

    boolean settlementPlaceable(String colour, int position);

    boolean roadPlaceable(String colour, int position);
}
