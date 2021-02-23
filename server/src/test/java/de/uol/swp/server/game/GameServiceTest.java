package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.Inventory;
import de.uol.swp.common.game.request.BuyDevelopmentCardRequest;
import de.uol.swp.common.game.request.UpdateInventoryAfterTradeWithBankRequest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.lobby.ILobbyManagement;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.UserStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This is a test of the class used to handle the requests sent by the client regarding the game
 *
 * @author Maximilian Lindner
 * @author Alwin Bossert
 * @since 2021-02-23
 */
@SuppressWarnings("UnstableApiUsage")
public class GameServiceTest {

    private final EventBus bus = new EventBus();
    private final UserStore userStore = new MainMemoryBasedUserStore();
    private final UserManagement userManagement = new UserManagement(userStore);
    private final ILobbyManagement lobbyManagement = new LobbyManagement();
    private final AuthenticationService authenticationService = new AuthenticationService(bus, userManagement);
    private final LobbyService lobbyService = new LobbyService(lobbyManagement, authenticationService, bus);
    private IGameManagement gameManagement;
    private GameService gameService;

    /**
     * Helper method run before each test case
     * <p>
     * This method instantiates a new GameManagement and a new GameService so that
     * one test's Game objects don't interfere with another test's
     */
    @BeforeEach
    void setUp() {
        gameManagement = new GameManagement();
        gameService = new GameService(bus, gameManagement, lobbyService);
    }

    /**
     * Helper method run after each test case
     * <p>
     * This method resets the gameService and gameManagement variables to null
     */
    @AfterEach
    void tearDown() {
        gameService = null;
        gameManagement = null;
    }

    /**
     * Tests if the gameManagement handles a BuyDevelopmentCardRequest properly
     * <p>
     * A BuyDevelopmentCardRequest is posted onto the event bus and the user
     * wants to buy a DevelopmentCard.
     * <p>
     * This test fails if the users inventory or the bank inventory are not
     * updated properly.
     */
    @Test
    void buyDevelopmentCardTest() {
        User[] user = new User[3];
        user[0] = new UserDTO("Chuck", "Norris", "chuck@norris.com");
        user[1] = new UserDTO("Duck", "Morris", "duck@morris.com");
        user[2] = new UserDTO("Sylvester", "Stallone", "Sly@stall.com");
        Lobby lobby = new LobbyDTO("testlobby", user[0]);
        lobby.joinUser(user[1]);
        lobby.joinUser(user[2]);
        gameManagement.createGame(lobby, user[0]);
        Game game = gameManagement.getGame("testlobby");
        Inventory[] gameInventory = game.getInventories();
        gameInventory[0].setWool(5);
        gameInventory[0].setBrick(5);
        gameInventory[0].setGrain(5);
        gameInventory[0].setOre(5);
        gameInventory[0].setLumber(5);
        assertEquals(gameInventory[0].getWool(), 5);
        assertEquals(gameInventory[0].getBrick(), 5);
        assertEquals(gameInventory[0].getOre(), 5);
        assertEquals(gameInventory[0].getGrain(), 5);
        assertEquals(gameInventory[0].getLumber(), 5);
        int knightCards = 0;
        int roadBuildingCards = 0;
        int yearOfPlentyCards = 0;
        int monopolyCards = 0;
        int victoryPointCards = 0;
        List<String> bankInventory = game.getBankInventory();
        for (String value : bankInventory) {
            if (value.equals("knightCard")) knightCards++;
            if (value.equals("roadBuildingCard")) roadBuildingCards++;
            if (value.equals("yearOfPlentyCard")) yearOfPlentyCards++;
            if (value.equals("monopolyCard")) monopolyCards++;
            if (value.equals("victoryPointCard")) victoryPointCards++;
        }
        Message buyDevelopmentCardRequest = new BuyDevelopmentCardRequest(user[0], "testlobby");
        bus.post(buyDevelopmentCardRequest);
        Game game1 = gameManagement.getGame("testlobby");
        Inventory[] gameInventory1 = game1.getInventories();
        assertEquals(gameInventory1[0].getOre(), 4);
        assertEquals(gameInventory1[0].getWool(), 4);
        assertEquals(gameInventory1[0].getBrick(), 5);
        assertEquals(gameInventory1[0].getGrain(), 4);
        assertEquals(gameInventory1[0].getLumber(), 5);
        int newKnightCards = 0;
        int newRoadBuildingCards = 0;
        int newYearOfPlentyCards = 0;
        int newMonopolyCards = 0;
        int newVictoryPointCards = 0;
        List<String> newBankInventory = game1.getBankInventory();
        for (String s : newBankInventory) {
            if (s.equals("knightCard")) newKnightCards++;
            if (s.equals("roadBuildingCard")) newRoadBuildingCards++;
            if (s.equals("yearOfPlentyCard")) newYearOfPlentyCards++;
            if (s.equals("monopolyCard")) newMonopolyCards++;
            if (s.equals("victoryPointCard")) newVictoryPointCards++;
        }
        assertTrue(
                ((newKnightCards == knightCards - 1) || (newMonopolyCards == monopolyCards - 1) || (newVictoryPointCards == victoryPointCards - 1) || (newYearOfPlentyCards == yearOfPlentyCards - 1) || (newRoadBuildingCards == roadBuildingCards - 1)));
    }
    /**
     * Tests if the gameManagement handles a UpdateInventoryAfterTradeWithBankRequest properly
     * <p>
     * A UpdateInventoryAfterTradeWithBankRequest is posted onto the event bus and the user
     * wants trade a resource with the bank.
     * <p>
     * This test fails if the users inventory is not updated properly.
     */
    @Test
    void tradeResourceWithBankTest() {
        User[] user = new User[3];
        user[0] = new UserDTO("Chuck", "Norris", "chuck@norris.com");
        user[1] = new UserDTO("Duck", "Morris", "duck@morris.com");
        user[2] = new UserDTO("Sylvester", "Stallone", "Sly@stall.com");
        Lobby lobby = new LobbyDTO("testlobby", user[0]);
        lobby.joinUser(user[1]);
        lobby.joinUser(user[2]);
        gameManagement.createGame(lobby, user[0]);
        Game game = gameManagement.getGame("testlobby");
        Inventory[] gameInventory = game.getInventories();
        gameInventory[0].setWool(5);
        gameInventory[0].setBrick(5);
        gameInventory[0].setGrain(5);
        gameInventory[0].setOre(5);
        gameInventory[0].setLumber(5);
        assertEquals(gameInventory[0].getWool(), 5);
        assertEquals(gameInventory[0].getBrick(), 5);
        assertEquals(gameInventory[0].getOre(), 5);
        assertEquals(gameInventory[0].getGrain(), 5);
        assertEquals(gameInventory[0].getLumber(), 5);

        Message updateInventoryAfterTradeWithBankRequest = new UpdateInventoryAfterTradeWithBankRequest(user[0],
                                                                                                        "testlobby",
                                                                                                        "wool",
                                                                                                        "brick");
        bus.post(updateInventoryAfterTradeWithBankRequest);
        Game game1 = gameManagement.getGame("testlobby");
        Inventory[] gameInventory1 = game1.getInventories();
        assertEquals(gameInventory1[0].getLumber(), 5);
        assertEquals(gameInventory1[0].getWool(), 6);
        assertEquals(gameInventory1[0].getBrick(), 1);
        assertEquals(gameInventory1[0].getGrain(), 5);
        assertEquals(gameInventory1[0].getLumber(), 5);
    }
}
