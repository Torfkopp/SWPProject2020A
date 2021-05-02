package de.uol.swp.common.game.map.gamemapDTO;

import de.uol.swp.common.game.map.management.IIntersection;
import de.uol.swp.common.user.UserOrDummy;

/**
 * The interface Intersection with buildable.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-25
 */
public interface IIntersectionWithBuildable extends IIntersection {

    /**
     * Returns if the given user can build on this intersection.
     *
     * @param user The user
     *
     * @return If the user can build on this intersection.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-25
     */
    boolean isBuildableBy(UserOrDummy user);
}
