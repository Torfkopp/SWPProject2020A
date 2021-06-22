package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.user.UserOrDummy;

import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Specialised class for a set
 * of UserOrDummy objects
 *
 * @author Mario Fokken
 * @since 2021-06-16
 */
public class UserOrDummySet extends LinkedHashSet<UserOrDummy> {

    /**
     * Gets the UserOrDummy at the
     * specified position
     *
     * @param i The UserOrDummy's position in the set
     *
     * @return The UserOrDummy at the position
     *
     * @since 2021-06-22
     */
    public UserOrDummy get(int i) {
        Iterator<UserOrDummy> iter = this.iterator();
        while (i > 0) {
            iter.next();
            i--;
        }
        return iter.next();
    }
}
