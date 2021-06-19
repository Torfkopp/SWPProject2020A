package de.uol.swp.server.lobby;

import de.uol.swp.common.lobby.LobbyName;
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
 * @since 2019-10-08
 */
class LobbyDTOTest {

    private static final User defaultUser = new UserDTO(98, "marco", "marco", "marco@grawunder.de");
    private static final User notInLobbyUser = new UserDTO(99, "no", "marco", "no@grawunder.de");
    private static final ILobby defaultLobby = new LobbyDTO(new LobbyName("Testlobby"), defaultUser, "");

    private static final int NO_USERS = 10;
    private static final List<User> users;

    static {
        users = new ArrayList<>();
        for (int i = 0; i < NO_USERS; i++) {
            users.add(new UserDTO(i, "marco" + i, "marco" + i, "marco" + i + "@grawunder.de"));
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
        ILobby lobby = new LobbyDTO(new LobbyName("test"), defaultUser, "");

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
        ILobby lobby = new LobbyDTO(new LobbyName("test"), defaultUser, "");

        assertEquals(new LobbyName("test"), lobby.getName());
        assertEquals(1, lobby.getActor().size());
        assertEquals(defaultUser, lobby.getActor().iterator().next());
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
        ILobby newLobby = LobbyDTO.create(defaultLobby);

        // Test every attribute
        assertEquals(defaultLobby.getName(), newLobby.getName());
        assertEquals(defaultLobby.getOwner(), newLobby.getOwner());
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
        ILobby lobby = LobbyDTO.create(defaultLobby);

        lobby.joinUser(users.get(0));
        assertEquals(2, lobby.getActor().size());
        assertTrue(lobby.getActor().contains(users.get(0)));

        lobby.joinUser(users.get(0));
        assertEquals(2, lobby.getActor().size());

        lobby.joinUser(users.get(1));
        assertEquals(3, lobby.getActor().size());
        assertTrue(lobby.getActor().contains(users.get(1)));
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
        ILobby lobby = LobbyDTO.create(defaultLobby);
        users.forEach(lobby::joinUser);

        assertEquals(users.size() + 1, lobby.getActor().size());
        lobby.leaveUser(users.get(5));

        // +1 for owner, -1 for user who left
        assertEquals(users.size() + 1 - 1, lobby.getActor().size());
        assertFalse(lobby.getActor().contains(users.get(5)));
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
        ILobby lobby = LobbyDTO.create(defaultLobby);
        users.forEach(lobby::joinUser);

        lobby.leaveUser(defaultUser);

        assertNotEquals(defaultUser, lobby.getOwner());
        assertTrue(users.contains(lobby.getOwner()));
    }

    /**
     * Test to check whether the maxPlayers setting of a lobby
     * is updated correctly.
     * <p>
     * This test fails if the maxPlayers setting is not updated.
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    @Test
    void updateMaxPlayersTest() {
        ILobby lobby = LobbyDTO.create(defaultLobby);
        assertEquals(3, lobby.getMaxPlayers());

        lobby.setMaxPlayers(4);

        assertEquals(4, lobby.getMaxPlayers());
    }

    /**
     * Test to check whether the moveTime setting of a lobby
     * is updated correctly.
     * <p>
     * This test fails if the moveTime setting is not updated.
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    @Test
    void updateMoveTimeTest() {
        ILobby lobby = LobbyDTO.create(defaultLobby);
        assertEquals(120, lobby.getMoveTime());

        lobby.setMoveTime(42);

        assertEquals(42, lobby.getMoveTime());
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
        ILobby lobby = LobbyDTO.create(defaultLobby);
        users.forEach(lobby::joinUser);

        lobby.updateOwner(users.get(6));
        assertEquals(users.get(6), lobby.getOwner());

        assertThrows(IllegalArgumentException.class, () -> lobby.updateOwner(notInLobbyUser));
    }

    /**
     * Test to check whether the randomPlayfield setting of a lobby
     * is updated correctly.
     * <p>
     * This test fails if the randomPlayfield setting is not updated.
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    @Test
    void updateRandomPlayfieldEnabledTest() {
        ILobby lobby = LobbyDTO.create(defaultLobby);
        assertFalse(lobby.isRandomPlayFieldEnabled());

        lobby.setRandomPlayFieldEnabled(true);

        assertTrue(lobby.isRandomPlayFieldEnabled());
    }

    /**
     * Test to check whether the startUpPhase setting of a lobby
     * is updated correctly.
     * <p>
     * This test fails if the startUpPhase setting is not updated.
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    @Test
    void updateStartUpPhaseEnabledTest() {
        ILobby lobby = LobbyDTO.create(defaultLobby);
        assertFalse(lobby.isStartUpPhaseEnabled());

        lobby.setStartUpPhaseEnabled(true);

        assertTrue(lobby.isStartUpPhaseEnabled());
    }
}
