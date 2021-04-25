package de.uol.swp.common.game.map.gamemapDTO;

import de.uol.swp.common.game.map.Hexes.IGameHex;

import java.io.Serializable;

/**
 * An Interface for a GameMapDTO Class
 *
 * @author Temmo Junkhoff
 * @since 2021-04-08
 */
public interface IGameMap extends Serializable {

    /**
     * Gets the jagged array of hexes
     *
     * @return A jagged Array of hexes
     *
     * @author Temmo Junkhoff
     */
    IGameHex[][] getHexes();

    /**
     * Gets the jagged array of intersections with their surrounding edges
     *
     * @return A jagged array of intersections with their surrounding edges
     *
     * @author Temmo Junkhoff
     */
    IntersectionWithEdges[][] getIntersections();
}