package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.user.UserOrDummy;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * Specialised class for a set
 * of UserOrDummy objects
 *
 * @author Mario Fokken
 * @since 2021-06-16
 */
public class UserOrDummySet extends TreeSet<UserOrDummy> {

    public UserOrDummy get(int i) {
        Iterator<UserOrDummy> iter = this.iterator();
        int j = 0;
        while (j < i) {
            iter.next();
            j++;
        }
        return iter.next();
    }
}
