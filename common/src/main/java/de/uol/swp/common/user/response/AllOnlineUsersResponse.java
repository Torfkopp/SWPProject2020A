package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Response message for the RetrieveAllOnlineUsersRequest
 *
 * This message gets sent to the client that sent an RetrieveAllOnlineUsersRequest.
 * It contains a List with User objects of every user currently logged in to the
 * server.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @see de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest
 * @see de.uol.swp.common.user.User
 * @since 2019-08-13
 */
public class AllOnlineUsersResponse extends AbstractResponseMessage {

    final private List<User> users = new ArrayList<>();

    /**
     * Default Constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2019-08-13
     */
    public AllOnlineUsersResponse(){
        // needed for serialization
    }

    /**
     * Constructor
     *
     * This constructor generates a new List of the logged in users from the given
     * Collection. The significant difference between the two being that the new
     * List contains copies of the User objects. These copies have their password
     * variable set to an empty String.
     *
     * @param users Collection of all users currently logged in
     * @since 2019-08-13
     */
    public AllOnlineUsersResponse(Collection<User> users) {
        for (User user : users) {
            this.users.add(UserDTO.createWithoutPassword(user));
        }
    }

    /**
     * Getter for the list of users currently logged in
     *
     * @return list of users currently logged in
     * @since 2019-08-13
     */
    public List<User> getUsers() {
        return users;
    }

}
