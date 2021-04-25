package de.uol.swp.common.game.map.gamemapDTO;

import de.uol.swp.common.game.map.management.Intersection;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.user.UserOrDummy;

import java.util.List;

/**
 * The type Intersection with buildable.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-25
 */
public class IntersectionWithBuildable extends Intersection implements IIntersectionWithBuildable {

    private List<UserOrDummy> buildable;

    /**
     * Instantiates a new Intersection.
     *
     * @param owner     the owner
     * @param state     the state
     * @param buildable the List of users that can build on this intersection
     *
     * @author Temmo Junkhoff
     * @since 2021-04-25
     */
    public IntersectionWithBuildable(Player owner, IntersectionState state,
                                     List<UserOrDummy> buildable) {
        this.buildable = buildable;
        super.setOwnerAndState(owner, state);
    }

    @Override
    public boolean isBuildableBy(UserOrDummy user) {
        return buildable.contains(user);
    }
}
