package de.uol.swp.common.game.map.gamemapDTO;

import de.uol.swp.common.game.map.hexes.IGameHex;

import java.io.Serializable;

/**
 * The GameMapDTO Class
 *
 * @author Temmo Junkhoff
 * @since 2021-04-08
 */
public class GameMapDTO implements IGameMap, Serializable {

    private final IGameHex[][] hexes;
    private final IntersectionWithEdges[][] intersections;

    /**
     * Constructor.
     *
     * @param hexes         The hexes
     * @param intersections The intersections
     *
     * @author Temmo Junkhoff
     * @since 2021-04-25
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
