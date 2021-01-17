package de.uol.swp.server.game.map;

/**
 * Class for the desert hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public class desertHex implements iGameMapManagement.iGameHex.iLandHex {
    private int[] neighbours;

    public desertHex(int[] neighbours) {
        this.neighbours = neighbours;
    }

    @Override
    public type type() {
        return type.Desert;
    }

    @Override
    public int[] getNeighbours() {
        return neighbours;
    }
}
