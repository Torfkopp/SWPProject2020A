package de.uol.swp.common.game.map.gamemapDTO;

import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.map.management.IIntersection;
import de.uol.swp.common.specialisedUtil.UserOrDummySet;
import de.uol.swp.common.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class IntersectionWithBuildableTest {

    @Test
    void isBuildableBy() {
        User defaultUser = mock(User.class);
        User secondUser = mock(User.class);
        UserOrDummySet buildableByList = new UserOrDummySet();
        buildableByList.add(defaultUser);

        IntersectionWithBuildable intersection = new IntersectionWithBuildable(Player.PLAYER_1,
                                                                               IIntersection.IntersectionState.FREE,
                                                                               buildableByList);

        assertTrue(intersection.isBuildableBy(defaultUser));
        assertFalse(intersection.isBuildableBy(secondUser));
    }
}