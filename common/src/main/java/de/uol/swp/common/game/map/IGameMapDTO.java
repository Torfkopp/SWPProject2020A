package de.uol.swp.common.game.map;

import de.uol.swp.common.game.map.Hexes.IGameHex;

import java.io.Serializable;

public interface IGameMapDTO extends Serializable {

    IGameHex[][] getHexes();

    IntersectionWithEdges[][] getIntersections();
}
