package de.uol.swp.common.lobby.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Response to the AllLobbyMembersRequest
 * <p>
 * This response includes all lobby members as well as the owner of the lobby.
 *
 * @author Alwin Bossert
 * @since 2020-12-20
 */
public class AllLobbyMembersResponse extends AbstractResponseMessage {

    private final List<UserDTO> users = new ArrayList<>();
    private User owner;

    /**
     * Default Constructor
     *
     * @implNote this constructor is needed for serialization
     */
    public AllLobbyMembersResponse() {
        // needed for serialization
    }

    /**
     * Constructor
     * <p>
     * This constructor generates a new List of the lobby members from the given
     * Collection. The significant difference between the two being that the new
     * List contains copies of the User objects. These copies have their password
     * variable set to an empty String.
     * The same method is used to provide the User object for the lobby owner.
     *
     * @param users Collection of all lobby members
     * @param owner Owner of the lobby
     * @since 2021-01-05
     */
    public AllLobbyMembersResponse(Collection<User> users, User owner) {
        for (User user : users) {
            this.users.add(UserDTO.createWithoutPassword(user));
        }
        this.owner = UserDTO.createWithoutPassword(owner);
    }

    /**
     * Getter for the list of lobby members
     *
     * @return list of lobby members
     */
    public List<UserDTO> getUsers() {
        return users;
    }

    /**
     * Getter for the Owner attribute
     *
     * @return Owner/Creator of the lobby
     * @since 2021-01-05
     */
    public User getOwner() {
        return owner;
    }
}
