package de.uol.swp.common.game.map.gamemapDTO;

import de.uol.swp.common.game.map.management.Edge;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.user.UserOrDummy;

import java.util.List;

/**
 * The type Edge with buildable.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-25
 */
public class EdgeWithBuildable extends Edge implements IEdgeWithBuildable {

    private List<UserOrDummy> buildable;

    /**
     * Instantiates a new Edge.
     *
     * @param orientation the orientation
     * @param owner       the owner
     * @param buildable   the List of users that can build on this edge
     *
     * @author Temmo Junkhoff
     * @since 2021-04-25
     */
    public EdgeWithBuildable(Orientation orientation, Player owner, List<UserOrDummy> buildable) {
        super(orientation, owner);
        this.buildable = buildable;
    }

    @Override
    public boolean isBuildableBy(UserOrDummy user) {
        if (buildable.contains(user)) return true;
        return false;
    }
}
