package de.uol.swp.common.game.map.management;

import java.io.Serializable;

/**
 * Represents a point on the game board distinguished by its type attribute.
 *
 * @author Temmo Junkhoff
 * @author Marvin Drees
 * @author Phillip-André Suhr
 * @since 2021-03-05
 */
public class MapPoint implements Serializable {

    private final Type type;
    private Integer x = null, y = null;
    private MapPoint l = null, r = null;

    /**
     * Enum for the types of MapPoints on the game board
     */
    public enum Type {
        HEX,
        EDGE,
        INTERSECTION,
        INVALID
    }

    /**
     * Private Constructor
     *
     * @param y    The y coordinate of this element
     * @param x    The x coordinate of this element
     * @param type The type of this element
     */
    private MapPoint(int y, int x, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    /**
     * Private Constructor
     *
     * @param l The neighbouring point to the left of this element
     * @param r The neighbouring point to the right of this element
     */
    private MapPoint(MapPoint l, MapPoint r) {
        this.l = l;
        this.r = r;
        this.type = Type.EDGE;
    }

    /**
     * Private Constructor
     *
     * @param type The type of this element
     */
    private MapPoint(Type type) {
        this.type = type;
    }

    /**
     * Creates a MapPoint representing an Edge, defined by its neighbours
     *
     * @param l The neighbouring point to the left of this Edge
     * @param r The neighbouring point to the right of this Edge
     *
     * @return MapPoint representing an Edge
     */
    public static MapPoint EdgeMapPoint(MapPoint l, MapPoint r) {
        //@formatter:off
        if (l == null
                || l.getType() == Type.INVALID
                || r == null
                || r.getType() == Type.INVALID
                || l.getType() != r.getType()
                || l.getType() == Type.EDGE)
            return InvalidMapPoint();
        //@formatter:on
        return new MapPoint(l, r);
    }

    /**
     * Creates a MapPoint representing a Hex, defined by its x,y coordinates
     *
     * @param y The y coordinate of this Hex
     * @param x The x coordinate of this Hex
     *
     * @return MapPoint representing a Hex
     */
    public static MapPoint HexMapPoint(int y, int x) {
        if (y < 0 || x < 0) return InvalidMapPoint();
        return new MapPoint(y, x, Type.HEX);
    }

    /**
     * Creates a MapPoint representing an Intersection, defined by its x,y coordinates
     *
     * @param y The y coordinate of this Intersection
     * @param x The x coordinate of this Intersection
     *
     * @return MapPoint representing an Intersection
     */
    public static MapPoint IntersectionMapPoint(int y, int x) {
        if (y < 0 || x < 0) return InvalidMapPoint();
        return new MapPoint(y, x, Type.INTERSECTION);
    }

    /**
     * Creates a MapPoint representing an invalid location on the game board
     *
     * @return MapPoint of type INVALID
     */
    public static MapPoint InvalidMapPoint() {
        return new MapPoint(Type.INVALID);
    }

    /**
     * Gets neighbouring point to the left of this element
     *
     * @return MapPoint representing the neighbour, or null if not applicable
     */
    public MapPoint getL() {
        return l;
    }

    /**
     * Gets neighbouring point to the right of this element
     *
     * @return MapPoint representing the neighbour, or null if not applicable
     */
    public MapPoint getR() {
        return r;
    }

    /**
     * Gets the type of the MapPoint
     *
     * @return Type of the MapPoint
     *
     * @see MapPoint.Type
     */
    public Type getType() {
        //@formatter:off
        if ((type == Type.HEX
                || type == Type.INTERSECTION)
                && l == null
                && r == null)
            return type;
        else if (type == Type.EDGE
                && x == null
                && y == null
                && ((l.getType() == Type.HEX && r.getType() == Type.HEX)
                || (l.getType() == Type.INTERSECTION && r.getType() == Type.INTERSECTION)))
            return type;
        else return Type.INVALID;
        //@formatter:on
    }

    /**
     * Gets the x coordinate of the MapPoint
     *
     * @return x coordinate of the MapPoint, or null if not applicable
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y coordinate of the MapPoint
     *
     * @return y coordinate of the MapPoint, or null if not applicable
     */
    public int getY() {
        return y;
    }

    /**
     * Checks whether this MapPoint has defined left and right neighbours,
     * which is only applicable for a MapPoint representing an Edge.
     *
     * @return true if the MapPoint is an Edge, false otherwise
     */
    public boolean hasLR() {
        return getType() == Type.EDGE;
    }

    /**
     * Checks whether this MapPoint has defined x,y coordinates,
     * which is only applicable for a MapPoint representing a Hex or an
     * Intersection.
     *
     * @return true if the MapPoint is a Hex or an Intersection, false if not
     */
    public boolean hasXY() {
        return (getType() == Type.HEX || getType() == Type.INTERSECTION);
    }
}
