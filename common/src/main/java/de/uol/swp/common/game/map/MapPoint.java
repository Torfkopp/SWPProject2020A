package de.uol.swp.common.game.map;

/**
 * MapPoint class
 * This class is used to store the coordinates of a hex or an intersection on the game map.
 */
public class MapPoint {

    int y;
    int x;

    /**
     * Position on the map defined via x and y
     *
     * @param y The y-coordinate of the point
     * @param x The x-coordiante of the point
     */
    public MapPoint(int y, int x) {
        this.y = y;
        this.x = x;
    }

    /**
     * Get x-coordinate
     *
     * @return x-coordinate as int
     */
    public int getX() {
        return x;
    }

    /**
     * Get y-coordinate
     *
     * @return y-coordinate as int
     */
    public int getY() {
        return y;
    }
}
