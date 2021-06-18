package de.uol.swp.client.specialisedUtil;

import de.uol.swp.common.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Specialised Class for an ObservableList
 * of Users in form of a string
 *
 * @author Mario Fokken
 * @since 2021-06-17
 */
public class UsersList {

    private final ObservableList<String> users;

    /**
     * Constructor
     */
    public UsersList() { users = FXCollections.observableArrayList(); }

    /**
     * Add a User to the list
     *
     * @param s The name of the User to add
     */
    public void add(String s) { users.add(s); }

    /**
     * Remove a User from the list
     *
     * @param s The name of the User to remove
     *
     * @return If the removal was successful
     */
    public boolean remove(String s) { return users.remove(s);}

    /**
     * Gets the list
     *
     * @return ObservableList of strings
     */
    public ObservableList<String> get() {
        return users;
    }

    /**
     * Clears the list and puts every
     * username into the list
     *
     * @param list A list of users to be put into the UsersList
     */
    public void update(List<User> list) {
        users.clear();
        list.forEach(u -> users.add(u.getUsername()));
    }
}
