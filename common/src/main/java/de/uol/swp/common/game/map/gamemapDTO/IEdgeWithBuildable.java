package de.uol.swp.common.game.map.gamemapDTO;

import de.uol.swp.common.game.map.management.IEdge;
import de.uol.swp.common.user.UserOrDummy;

public interface IEdgeWithBuildable extends IEdge {

    boolean isBuildableBy(UserOrDummy user);
}
