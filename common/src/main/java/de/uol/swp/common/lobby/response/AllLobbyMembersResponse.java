package de.uol.swp.common.lobby.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AllLobbyMembersResponse extends AbstractResponseMessage {

    private final List<UserDTO> users = new ArrayList<>();

    /**
     * Default Constructor
     *
     * @implNote This constructor is needed for serialisation
     * @since 2020.12.21
     */
    public AllLobbyMembersResponse() {
        // needed for serialisation
    }

    /**
     * Constructor
     * <p>
     * This constructor generates a new List of the lobby members from the given
     * Collection. The significant difference between the two is that the new
     * List contains copies of the User objects. These copies have their password
     * variable set to an empty String.
     *
     * @param users Collection of all lobby members
     * @since 2020.12.21
     */
    public AllLobbyMembersResponse(Collection<User> users) {
        for (User user : users) {
            this.users.add(UserDTO.createWithoutPassword(user));
        }
    }

    /**
     * Gets the list of lobby members
     *
     * @return List of lobby members
     * @since 2020.12.21
     */
    public List<UserDTO> getUsers() {
        return users;
    }
}
