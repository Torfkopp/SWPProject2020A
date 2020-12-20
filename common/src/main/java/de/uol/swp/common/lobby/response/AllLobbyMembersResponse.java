package de.uol.swp.common.lobby.response;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AllLobbyMembersResponse extends AbstractResponseMessage {

    final private List<UserDTO> users = new ArrayList<>();

    /**
     * Default Constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2019-08-13
     */
    public AllLobbyMembersResponse(){
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
    public AllLobbyMembersResponse(Collection<User> users) {
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
    public List<UserDTO> getUsers() {
        return users;
    }
}
