package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.user.Actor;

import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Specialised class for a set
 * of Actor objects
 *
 * @author Mario Fokken
 * @since 2021-06-16
 */
public class ActorSet extends LinkedHashSet<Actor> {

    /**
     * Gets the Actor at the
     * specified position
     *
     * @param i The Actor's position in the set
     *
     * @return The Actor at the position
     *
     * @since 2021-06-22
     */
    public Actor get(int i) {
        Iterator<Actor> iter = this.iterator();
        while (i > 0) {
            iter.next();
            i--;
        }
        return iter.next();
    }
}
