package de.uol.swp.common.game.map.gamemapDTO;

import de.uol.swp.common.game.map.management.IEdge;
import de.uol.swp.common.user.UserOrDummy;

/**
 * The interface Edge with buildable.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-25
 */
public interface IEdgeWithBuildable extends IEdge {

    /**
     * Returns if the given user can build on this edge.
     *
     * @param user the user
     *
     * @return If the user can build on this edge.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-25
     */
    boolean isBuildableBy(UserOrDummy user);
}
