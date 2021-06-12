package de.uol.swp.common.game.map.gamemapDTO;

import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.map.management.IIntersection;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class IntersectionWithBuildableTest {

    @Test
    void isBuildableBy() {
        User defaultUser = mock(User.class);
        User secondUser = mock(User.class);
        List<UserOrDummy> buildableByList = new ArrayList<>();
        buildableByList.add(defaultUser);

        IntersectionWithBuildable intersection = new IntersectionWithBuildable(Player.PLAYER_1,
                                                                               IIntersection.IntersectionState.FREE,
                                                                               buildableByList);

        assertTrue(intersection.isBuildableBy(defaultUser));
        assertFalse(intersection.isBuildableBy(secondUser));
    }
}