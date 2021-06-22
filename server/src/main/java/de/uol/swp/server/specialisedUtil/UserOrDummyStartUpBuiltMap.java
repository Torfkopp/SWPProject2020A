package de.uol.swp.server.specialisedUtil;

import de.uol.swp.common.game.StartUpPhaseBuiltStructures;
import de.uol.swp.common.user.UserOrDummy;

import java.util.HashMap;

import static de.uol.swp.common.game.StartUpPhaseBuiltStructures.*;

/**
 * Specialised class to mao
 * a UserOrDummy to a StartUpPhaseBuiltStructure
 *
 * @author Mario Fokken
 * @since 2021-06-16
 */
public class UserOrDummyStartUpBuiltMap extends HashMap<UserOrDummy, StartUpPhaseBuiltStructures> {

    /**
     * Returns whether the user has
     * completed the StartUpPhase
     *
     * @param user The user to check the progress of
     *
     * @return True if finished
     */
    public boolean finished(UserOrDummy user) {
        return get(user) == ALL_BUILT;
    }

    /**
     * Puts the specified user
     * into the next phase
     *
     * @param user The user who built
     */
    public void nextPhase(UserOrDummy user) {
        if (get(user) == NONE_BUILT) put(user, FIRST_BOTH_BUILT);
        else put(user, ALL_BUILT);
    }

    /**
     * Puts a user with NONE_BUILT into the map
     *
     * @param user The user starting building
     */
    public void put(UserOrDummy user) {
        super.put(user, NONE_BUILT);
    }
}
