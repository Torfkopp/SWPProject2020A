package de.uol.swp.common.game.map.gamemapDTO;

import de.uol.swp.common.game.map.management.Intersection;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.user.UserOrDummy;

import java.util.List;

public class IntersectionWithBuildable extends Intersection implements IIntersectionWithBuildable {

    private List<UserOrDummy> buildable;

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
