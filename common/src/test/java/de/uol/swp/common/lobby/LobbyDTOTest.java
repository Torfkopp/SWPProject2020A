package de.uol.swp.common.lobby;

import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the UserDTO class
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.user.UserDTO
 * @see de.uol.swp.common.lobby.Lobby
 * @see de.uol.swp.common.lobby.dto.LobbyDTO
 * @since 2019-10-08
 */
class LobbyDTOTest {

    private static final User defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de");
    private static final User notInLobbyUser = new UserDTO("no", "marco", "no@grawunder.de");
    private static final Lobby defaultLobby = new LobbyDTO("TestLobby", defaultUser, false);

    private static final int NO_USERS = 10;
    private static final List<User> users;

    static {
        users = new ArrayList<>();
        for (int i = 0; i < NO_USERS; i++) {
            users.add(new UserDTO("marco" + i, "marco" + i, "marco" + i + "@grawunder.de"));
        }
        Collections.sort(users);
    }

    /**
     * This test checks if a lobby can be empty.
     * <p>
     * If the leaveUser method does not throw an exception, this test fails.
     *
     * @since 2019-10-08
     */
    @Test
    void assureNonEmptyLobbyTest() {
        Lobby lobby = new LobbyDTO("test", defaultUser, false);

        assertThrows(IllegalArgumentException.class, () -> lobby.leaveUser(defaultUser));
    }

    /**
     * This test checks if a lobby is created correctly.
     * <p>
     * If the variables are not set correctly, this test fails.
     *
     * @since 2019-10-08
     */
    @Test
    void createLobbyTest() {
        Lobby lobby = new LobbyDTO("test", defaultUser, false);

        assertEquals(lobby.getName(), "test");
        assertEquals(lobby.getUsers().size(), 1);
        assertEquals(lobby.getUsers().iterator().next(), defaultUser);
    }

    /**
     * This test checks if the copy constructor works correctly.
     * <p>
     * This test fails if any of the fields mismatch or the objects are not considered equal.
     *
     * @since 2020-12-05
     */
    @Test
    void createWithExistingLobby() {
        Lobby newLobby = LobbyDTO.create(defaultLobby);

        // Test every attribute
        assertEquals(newLobby.getName(), defaultLobby.getName());
        assertEquals(newLobby.getOwner(), defaultLobby.getOwner());
    }

    /**
     * This test checks if a user can join a lobby.
     * <p>
     * The test fails when the size of the lobby's user list does not get bigger,
     * or when a newly joined user is not in the list.
     *
     * @since 2019-10-08
     */
    @Test
    void joinUserLobbyTest() {
        Lobby lobby = LobbyDTO.create(defaultLobby);

        lobby.joinUser(users.get(0));
        assertEquals(lobby.getUsers().size(), 2);
        assertTrue(lobby.getUsers().contains(users.get(0)));

        lobby.joinUser(users.get(0));
        assertEquals(lobby.getUsers().size(), 2);

        lobby.joinUser(users.get(1));
        assertEquals(lobby.getUsers().size(), 3);
        assertTrue(lobby.getUsers().contains(users.get(1)));
    }

    /**
     * This test checks if a user can leave a lobby.
     * <p>
     * The test fails when the size of the lobby's user list does not get smaller,
     * or when the user who left remains in the list.
     *
     * @since 2019-10-08
     */
    @Test
    void leaveUserLobbyTest() {
        Lobby lobby = LobbyDTO.create(defaultLobby);
        users.forEach(lobby::joinUser);

        assertEquals(lobby.getUsers().size(), users.size() + 1);
        lobby.leaveUser(users.get(5));

        assertEquals(lobby.getUsers().size(), users.size() + 1 - 1);
        assertFalse(lobby.getUsers().contains(users.get(5)));
    }

    /**
     * Test to check if the owner can leave the lobby correctly
     * <p>
     * This test fails if the owner field is not updated, if the owner leaves the
     * lobby, or if he remains in the lobby's user list.
     *
     * @since 2019-10-08
     */
    @Test
    void removeOwnerFromLobbyTest() {
        Lobby lobby = LobbyDTO.create(defaultLobby);
        users.forEach(lobby::joinUser);

        lobby.leaveUser(defaultUser);

        assertNotEquals(lobby.getOwner(), defaultUser);
        //Code Analysis: "Suspicious call to 'List.contains'" -Mario
        assertTrue(users.contains(lobby.getOwner()));
    }

    /**
     * This test checks if the owner of a lobby is in it
     * and if he is updatable.
     * <p>
     * This test fails when the owner is not in the lobby,
     * or when he's not updatable.
     *
     * @since 2019-10-08
     */
    @Test
    void updateOwnerTest() {
        Lobby lobby = LobbyDTO.create(defaultLobby);
        users.forEach(lobby::joinUser);

        lobby.updateOwner(users.get(6));
        assertEquals(lobby.getOwner(), users.get(6));

        assertThrows(IllegalArgumentException.class, () -> lobby.updateOwner(notInLobbyUser));
    }
}
