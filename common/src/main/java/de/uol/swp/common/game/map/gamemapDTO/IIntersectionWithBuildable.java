package de.uol.swp.common.game.map.gamemapDTO;

import de.uol.swp.common.game.map.management.IIntersection;
import de.uol.swp.common.user.UserOrDummy;

public interface IIntersectionWithBuildable extends IIntersection {

    boolean isBuildableBy(UserOrDummy user);
}
