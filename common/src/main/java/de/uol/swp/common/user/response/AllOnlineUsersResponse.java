package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Response message for the RetrieveAllOnlineUsersRequest
 * <p>
 * This message gets sent to the origin of an RetrieveAllOnlineUsersRequest.
 * It contains a list with User objects of every user currently logged in to the
 * server.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @see de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest
 * @see de.uol.swp.common.user.User
 * @since 2019-08-13
 */
public class AllOnlineUsersResponse extends AbstractResponseMessage {

    final private List<UserDTO> users = new ArrayList<>();

    /**
     * Default constructor
     *
     * @implNote This constructor is needed for serialisation
     * @since 2019-08-13
     */
    public AllOnlineUsersResponse() {
        // needed for serialisation
    }

    /**
     * Constructor
     * <p>
     * This constructor generates a new list of logged in users from the given
     * collection. The significant difference between these two is
     * that the new list contains copies of the User objects.
     * These copies have their password variable set to an empty string.
     *
     * @param users Collection of all currently logged in users
     * @since 2019-08-13
     */
    public AllOnlineUsersResponse(Collection<User> users) {
        for (User user : users) {
            this.users.add(UserDTO.createWithoutPassword(user));
        }
    }

    /**
     * Gets the list of all currently logged in users
     *
     * @return list of currently logged in users
     * @since 2019-08-13
     */
    public List<UserDTO> getUsers() {
        return users;
    }
}
