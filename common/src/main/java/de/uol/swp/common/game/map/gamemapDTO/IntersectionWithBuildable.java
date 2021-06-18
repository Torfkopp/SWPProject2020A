package de.uol.swp.common.game.map.gamemapDTO;

import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.map.management.Intersection;
import de.uol.swp.common.specialisedUtil.UserOrDummyList;
import de.uol.swp.common.user.UserOrDummy;

/**
 * The type Intersection with buildable.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-25
 */
public class IntersectionWithBuildable extends Intersection implements IIntersectionWithBuildable {

    private final UserOrDummyList buildable;

    /**
     * Instantiates a new Intersection.
     *
     * @param owner     The owner
     * @param state     The state
     * @param buildable The List of users that can build on this intersection
     *
     * @author Temmo Junkhoff
     * @since 2021-04-25
     */
    public IntersectionWithBuildable(Player owner, IntersectionState state, UserOrDummyList buildable) {
        this.buildable = buildable;
        super.setOwnerAndState(owner, state);
    }

    @Override
    public boolean isBuildableBy(UserOrDummy user) {
        return buildable.contains(user);
    }
}
