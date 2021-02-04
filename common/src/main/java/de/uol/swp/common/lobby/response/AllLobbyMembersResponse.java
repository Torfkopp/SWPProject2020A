package de.uol.swp.common.lobby.response;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.*;

/**
 * Response to the AllLobbyMembersRequest
 * <p>
 * This response includes all lobby members as well as the owner of the lobby.
 *
 * @author Alwin Bossert
 * @since 2020-12-20
 */
public class AllLobbyMembersResponse extends AbstractLobbyResponse {

    private final List<User> users = new ArrayList<>();
    private final Set<User> readyUsers = new TreeSet<>();
    private User owner;

    /**
     * Default Constructor
     *
     * @implNote This constructor is needed for serialisation
     * @since 2020-12-21
     */
    public AllLobbyMembersResponse() {
        super();
        // needed for serialisation
    }

    /**
     * Constructor
     * <p>
     * This constructor generates a new List of the lobby members from the given
     * Collection. The significant difference between the two is that the new
     * List contains copies of the User objects. These copies have their password
     * variable set to an empty String.
     * The same is done for the Set of ready users in the lobby.
     * The same method is used to provide the User object for the lobby owner.
     *
     * @param users      Collection of all lobby members
     * @param owner      Owner of the lobby
     * @param readyUsers Set of all ready lobby members
     * @param lobbyName  Name of the lobby
     *
     * @since 2021-02-04
     */
    public AllLobbyMembersResponse(Collection<User> users, User owner, Set<User> readyUsers, String lobbyName) {
        super(lobbyName);
        for (User user : users) {
            this.users.add(UserDTO.createWithoutPassword(user));
        }
        for (User user : readyUsers) {
            this.readyUsers.add(UserDTO.createWithoutPassword(user));
        }
        this.owner = UserDTO.createWithoutPassword(owner);
    }

    /**
     * Getter for the Owner attribute
     *
     * @return Owner/Creator of the lobby
     *
     * @since 2021-01-05
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Gets the set of all ready users
     *
     * @return A Set of ready Users
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @since 2021-01-19
     */
    public Set<User> getReadyUsers() {
        return this.readyUsers;
    }

    /**
     * Gets the list of lobby members
     *
     * @return List of lobby members
     *
     * @since 2020-12-21
     */
    public List<User> getUsers() {
        return users;
    }
}
