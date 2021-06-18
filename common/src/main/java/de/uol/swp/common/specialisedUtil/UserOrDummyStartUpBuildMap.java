package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.game.StartUpPhaseBuiltStructures;
import de.uol.swp.common.user.UserOrDummy;

import java.util.HashMap;

import static de.uol.swp.common.game.StartUpPhaseBuiltStructures.*;

public class UserOrDummyStartUpBuildMap extends HashMap<UserOrDummy, StartUpPhaseBuiltStructures> {

    public boolean finished(UserOrDummy user) {
        return get(user) == ALL_BUILT;
    }

    public void nextPhase(UserOrDummy user) {
        if (get(user) == NONE_BUILT) put(user, FIRST_BOTH_BUILT);
        else put(user, ALL_BUILT);
    }

    public void put(UserOrDummy user) {
        super.put(user, NONE_BUILT);
    }
}
