package de.uol.swp.common.game.map;

import de.uol.swp.common.game.map.Hexes.IGameHex;

import java.io.Serializable;

/**
 * The GameMapDTO Class
 *
 * @author Aldin Dervisi
 * @author Temmo Junkhoff
 * @since 2021-04-08
 */
public class GameMapDTO implements IGameMap, Serializable {

    private final IGameHex[][] hexes;
    private final IntersectionWithEdges[][] intersections;

    /**
     * Constructor
     *
     * @param hexes         The hexes of the gamemap
     * @param intersections The intersections with their surround edges
     */
    public GameMapDTO(IGameHex[][] hexes, IntersectionWithEdges[][] intersections) {
        this.hexes = hexes;
        this.intersections = intersections;
    }

    @Override
    public IGameHex[][] getHexes() {
        return hexes;
    }

    @Override
    public IntersectionWithEdges[][] getIntersections() {
        return intersections;
    }
}