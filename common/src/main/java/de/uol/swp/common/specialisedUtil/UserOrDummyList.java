package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.user.UserOrDummy;

import java.util.ArrayList;

/**
 * Specialised class for a list of
 * UserOrDummy objects
 *
 * @author Mario Fokken
 * @since 2021-06-16
 */
public class UserOrDummyList extends ArrayList<UserOrDummy> {

    /**
     * Default constructor
     */
    public UserOrDummyList() {super();}

    /**
     * Constructor with a Set
     *
     * @param users UserOrDummySet Object
     */
    public UserOrDummyList(UserOrDummySet users) {
        super(users);
    }
}
