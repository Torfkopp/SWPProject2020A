package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.AIDTO;
import de.uol.swp.common.user.UserOrDummy;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VictoryPointOverTimeMapTest {

    @Test
    void test() {
        VictoryPointOverTimeMap map = new VictoryPointOverTimeMap();
        UserOrDummy user = new AIDTO(AI.Difficulty.EASY);
        Map<Integer, Integer> numbers = new HashMap<>();
        map.put(user, numbers);
        assertTrue(map.containsKey(user));
        assertTrue(map.containsValue(numbers));
    }
}
