package de.uol.swp.common.game.map;

import java.io.Serializable;
import java.util.Set;

public interface IIntersectionWithEdges extends Serializable {

    Set<IEdge> getEdges();

    IIntersection getIntersection();
}
