package de.uol.swp.common.game.map.gamemapDTO;

import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.map.management.IEdge;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class EdgeWithBuildableTest {

    @Test
    void isBuildableBy() {
        User defaultUser = mock(User.class);
        User secondUser = mock(User.class);
        List<UserOrDummy> buildableByList = new ArrayList<>();
        buildableByList.add(defaultUser);

        EdgeWithBuildable edge = new EdgeWithBuildable(IEdge.Orientation.SOUTH, Player.PLAYER_1, buildableByList);

        assertTrue(edge.isBuildableBy(defaultUser));
        assertFalse(edge.isBuildableBy(secondUser));
    }
}