package de.uol.swp.common.game.map.gamemapDTO;

import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.map.management.Edge;
import de.uol.swp.common.user.Actor;

import java.util.List;

/**
 * A Class to store an edge with the players that can build on it.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-25
 */
public class EdgeWithBuildable extends Edge implements IEdgeWithBuildable {

    private final List<Actor> buildable;

    /**
     * Instantiates a new Edge.
     *
     * @param orientation The orientation
     * @param owner       The owner
     * @param buildable   The List of users that can build on this edge
     *
     * @author Temmo Junkhoff
     * @since 2021-04-25
     */
    public EdgeWithBuildable(Orientation orientation, Player owner, List<Actor> buildable) {
        super(orientation, owner);
        this.buildable = buildable;
    }

    @Override
    public boolean isBuildableBy(Actor user) {
        return buildable.contains(user);
    }
}
