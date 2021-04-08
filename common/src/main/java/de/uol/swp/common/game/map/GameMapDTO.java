package de.uol.swp.common.game.map;

import de.uol.swp.common.game.map.Hexes.IGameHex;

import java.io.Serializable;

public class GameMapDTO implements IGameMapDTO, Serializable {

    IGameHex[][] hexes;
    IntersectionWithEdges[][] intersections;

    public GameMapDTO(IGameHex[][] hexes, IntersectionWithEdges[][] intersections) {
        this.hexes = hexes;
        this.intersections = intersections;
    }

    public IGameHex[][] getHexes() {
        return hexes;
    }

    public IntersectionWithEdges[][] getIntersections() {
        return intersections;
    }
}
