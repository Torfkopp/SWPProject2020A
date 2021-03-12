package de.uol.swp.common.lobby.response;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.UserOrDummy;

import java.util.*;

/**
 * Response to the AllLobbyMembersRequest
 * <p>
 * This response includes all lobby members as well as the owner of the lobby.
 *
 * @author Alwin Bossert
 * @see de.uol.swp.common.lobby.request.RetrieveAllLobbyMembersRequest
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2020-12-20
 */
public class AllLobbyMembersResponse extends AbstractLobbyResponse {

    private final List<UserOrDummy> users = new ArrayList<>();
    private final Set<UserOrDummy> readyUsers = new TreeSet<>();
    private final User owner;

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
     * @param lobbyName  The name of the lobby
     * @param users      Collection of all lobby members
     * @param owner      Owner of the lobby
     * @param readyUsers Set of all ready lobby members
     *
     * @since 2021-01-19
     */
    public AllLobbyMembersResponse(String lobbyName, Set<UserOrDummy> users, User owner, Set<UserOrDummy> readyUsers) {
        super(lobbyName);
        for (UserOrDummy user : users) {
            if (user instanceof User) this.users.add(UserDTO.createWithoutPassword((User) user));
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
    public Set<UserOrDummy> getReadyUsers() {
        return this.readyUsers;
    }

    /**
     * Gets the list of lobby members
     *
     * @return List of lobby members
     *
     * @since 2020-12-21
     */
    public List<UserOrDummy> getUsers() {
        return users;
    }
}
