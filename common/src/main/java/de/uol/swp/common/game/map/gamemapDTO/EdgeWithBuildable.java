package de.uol.swp.common.game.map.gamemapDTO;

import de.uol.swp.common.game.map.management.Edge;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.user.UserOrDummy;

import java.util.List;

public class EdgeWithBuildable extends Edge implements IEdgeWithBuildable {

    private List<UserOrDummy> buildable;

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
