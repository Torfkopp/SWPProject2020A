//package de.uol.swp.client.lobby.event;
//
//import de.uol.swp.common.lobby.Lobby;
//import de.uol.swp.common.lobby.LobbyDTO;
//import de.uol.swp.common.user.User;
//import de.uol.swp.common.user.UserDTO;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
///**
// * Test for the event used to communicate Lobby details to new
// * LobbyPresenter instances
// *
// * @author Temmo Junkhoff
// * @author Phillip-André Suhr
// * @see de.uol.swp.client.lobby.event.LobbyUpdateEvent
// * @since 2021-01-03
// */
//class LobbyUpdateEventTest {
//
//    private static final User defaultUser = new UserDTO(1, "I'm tree", "I'm pretty sure I'm a tree", "tree@tree.test");
//    private static final Lobby defaultLobby = new LobbyDTO("Am I a lobby?", defaultUser, false, 4, false, 60, true,
//                                                           true);
//
//    /**
//     * Test for the creation of LobbyUpdateEvents
//     * <p>
//     * This checks if the lobby name and related User details are set
//     * correctly during the creation of a new event
//     */
//    @Test
//    void createLobbyUpdateEventTest() {
//        LobbyUpdateEvent event = new LobbyUpdateEvent(defaultLobby);
//
//        assertEquals(defaultLobby, event.getLobby());
//        assertEquals(defaultLobby.getName(), event.getLobby().getName());
//        assertEquals(defaultLobby.isInGame(), event.getLobby().isInGame());
//        assertEquals(defaultLobby.getMaxPlayers(), event.getLobby().getMaxPlayers());
//        assertEquals(defaultLobby.commandsAllowed(), event.getLobby().commandsAllowed());
//        assertEquals(defaultLobby.getMoveTime(), event.getLobby().getMoveTime());
//        assertEquals(defaultLobby.startUpPhaseEnabled(), event.getLobby().startUpPhaseEnabled());
//        assertEquals(defaultLobby.randomPlayfieldEnabled(), event.getLobby().randomPlayfieldEnabled());
//    }
//}
