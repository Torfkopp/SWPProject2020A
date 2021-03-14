package de.uol.swp.common.game.map;

public class BetterMapPoint {

    private final Type type;
    private Integer x = null, y = null;
    private BetterMapPoint l = null, r = null;

    public enum Type {
        HEX,
        EDGE,
        INTERSECTION,
        INVALID
    }

    private BetterMapPoint(int y, int x, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    private BetterMapPoint(BetterMapPoint l, BetterMapPoint r) {
        this.l = l;
        this.r = r;
        this.type = Type.EDGE;
    }

    private BetterMapPoint(Type type) {
        this.type = type;
    }

    public static BetterMapPoint EdgeMapPoint(BetterMapPoint l, BetterMapPoint r) {
        return new BetterMapPoint(l, r);
    }

    public static BetterMapPoint HexMapPoint(int y, int x) {
        return new BetterMapPoint(y, x, Type.HEX);
    }

    public static BetterMapPoint IntersectionMapPoint(int y, int x) {
        return new BetterMapPoint(y, x, Type.INTERSECTION);
    }

    public static BetterMapPoint InvalidMapPoint() {
        return new BetterMapPoint(Type.INVALID);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Type getType() {
        //@formatter:off
        if ((type == Type.HEX || type == Type.INTERSECTION) && l == null && r == null) return type;
        else if (type == Type.EDGE && x == null && y == null
                 && ((l.getType() == Type.HEX && r.getType() == Type.HEX)
                     || (l.getType() == Type.INTERSECTION && r.getType() == Type.INTERSECTION)))
            return type;
        else return Type.INVALID;
        //@formatter:on
    }

    public BetterMapPoint getL() {
        return l;
    }

    public BetterMapPoint getR() {
        return r;
    }
}
