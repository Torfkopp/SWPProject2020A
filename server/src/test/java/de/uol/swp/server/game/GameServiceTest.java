package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.game.request.AcceptUserTradeRequest;
import de.uol.swp.common.game.request.BuyDevelopmentCardRequest;
import de.uol.swp.common.game.request.ExecuteTradeWithBankRequest;
import de.uol.swp.common.game.request.PlayCardRequest.PlayKnightCardRequest;
import de.uol.swp.common.game.request.PlayCardRequest.PlayMonopolyCardRequest;
import de.uol.swp.common.game.request.PlayCardRequest.PlayYearOfPlentyCardRequest;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.BankInventory;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.Inventory;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.game.robber.RobberChosenVictimRequest;
import de.uol.swp.common.game.robber.RobberNewPositionChosenRequest;
import de.uol.swp.common.game.robber.RobberTaxChosenRequest;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.request.KickUserRequest;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.*;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.server.game.map.IGameMapManagement;
import de.uol.swp.server.lobby.*;
import de.uol.swp.server.sessionmanagement.SessionManagement;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.UserStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType.*;
import static de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType.*;
import static org.junit.jupiter.api.Assertions.*;

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
    private final SessionManagement sessionManagement = new SessionManagement();
    private final LobbyService lobbyService = new LobbyService(lobbyManagement, sessionManagement, bus);
    private final AuthenticationService authenticationService = new AuthenticationService(bus, userManagement,
                                                                                          sessionManagement);
    private final LobbyName defaultLobby = new LobbyName("testLobby");
    private IGameManagement gameManagement;
    private GameService gameService;

    /**
     * Helper method run before each test case
     * <p>
     * This method instantiates a new GameManagement and a new GameService so that
     * one test's Game objects don't interfere with another test's
     */
    @BeforeEach
    protected void setUp() {
        gameManagement = new GameManagement(lobbyManagement);
        gameService = new GameService(bus, gameManagement, lobbyManagement, lobbyService);
    }

    /**
     * Helper method run after each test case
     * <p>
     * This method resets the gameService and gameManagement variables to null
     */
    @AfterEach
    protected void tearDown() {
        gameService = null;
        gameManagement = null;
    }

    /**
     * Tests if the gameManagement handles a BuyDevelopmentCardRequest properly when the
     * bankInventory is empty
     * <p>
     * A BuyDevelopmentCardRequest is posted onto the event bus and the user
     * wants to buy a DevelopmentCard.
     * <p>
     * This test fails if the users gets a development card even if the banks inventory is empty
     */
    @Test
    void BuyDevelopmentCardWhenBankInventoryIsEmptyTest() {
        User[] user = new User[3];
        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
        ILobby lobby = new LobbyDTO(defaultLobby, user[0], null);
        lobby.joinUser(user[1]);
        lobby.joinUser(user[2]);
        IGameMapManagement gameMap = new GameMapManagement();
        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
        gameManagement.createGame(lobby, user[0], gameMap, 0);
        Game game = gameManagement.getGame(defaultLobby);
        Inventory[] gameInventory = game.getAllInventories();
        gameInventory[0].set(WOOL, 5);
        gameInventory[0].set(BRICK, 5);
        gameInventory[0].set(GRAIN, 5);
        gameInventory[0].set(ORE, 5);
        gameInventory[0].set(LUMBER, 5);
        assertEquals(5, gameInventory[0].get(WOOL));
        assertEquals(5, gameInventory[0].get(BRICK));
        assertEquals(5, gameInventory[0].get(ORE));
        assertEquals(5, gameInventory[0].get(GRAIN));
        assertEquals(5, gameInventory[0].get(LUMBER));

        assertEquals(0, gameInventory[0].get(KNIGHT_CARD));
        assertEquals(0, gameInventory[0].get(ROAD_BUILDING_CARD));
        assertEquals(0, gameInventory[0].get(MONOPOLY_CARD));
        assertEquals(0, gameInventory[0].get(YEAR_OF_PLENTY_CARD));
        assertEquals(0, gameInventory[0].get(VICTORY_POINT_CARD));

        BankInventory bankInventory = game.getBankInventory();
        //deletes the bank inventory
        for (DevelopmentCardType developmentCardType : DevelopmentCardType.values()) {
            bankInventory.set(developmentCardType, 0);
        }

        Message buyDevelopmentCardRequest = new BuyDevelopmentCardRequest(user[0], defaultLobby);
        bus.post(buyDevelopmentCardRequest);
        Game game1 = gameManagement.getGame(defaultLobby);
        Inventory[] gameInventory1 = game1.getAllInventories();
        BankInventory bankInv = game1.getBankInventory();
        assertEquals(bankInventory, bankInv);
        assertEquals(0, gameInventory1[0].get(KNIGHT_CARD));
        assertEquals(0, gameInventory1[0].get(ROAD_BUILDING_CARD));
        assertEquals(0, gameInventory1[0].get(MONOPOLY_CARD));
        assertEquals(0, gameInventory1[0].get(YEAR_OF_PLENTY_CARD));
        assertEquals(0, gameInventory1[0].get(VICTORY_POINT_CARD));
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
        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
        ILobby lobby = new LobbyDTO(defaultLobby, user[0], null);
        lobby.joinUser(user[1]);
        lobby.joinUser(user[2]);
        IGameMapManagement gameMap = new GameMapManagement();
        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
        gameManagement.createGame(lobby, user[0], gameMap, 0);
        Game game = gameManagement.getGame(defaultLobby);
        Inventory[] gameInventory = game.getAllInventories();
        gameInventory[0].set(WOOL, 5);
        gameInventory[0].set(BRICK, 5);
        gameInventory[0].set(GRAIN, 5);
        gameInventory[0].set(ORE, 5);
        gameInventory[0].set(LUMBER, 5);
        assertEquals(5, gameInventory[0].get(WOOL));
        assertEquals(5, gameInventory[0].get(BRICK));
        assertEquals(5, gameInventory[0].get(ORE));
        assertEquals(5, gameInventory[0].get(GRAIN));
        assertEquals(5, gameInventory[0].get(LUMBER));
        assertEquals(0, gameInventory[0].get(KNIGHT_CARD));
        assertEquals(0, gameInventory[0].get(ROAD_BUILDING_CARD));
        assertEquals(0, gameInventory[0].get(MONOPOLY_CARD));
        assertEquals(0, gameInventory[0].get(YEAR_OF_PLENTY_CARD));
        assertEquals(0, gameInventory[0].get(VICTORY_POINT_CARD));

        game.setDiceRolledAlready(true);
        Message buyDevelopmentCardRequest = new BuyDevelopmentCardRequest(user[0], defaultLobby);
        bus.post(buyDevelopmentCardRequest);
        Game game1 = gameManagement.getGame(defaultLobby);
        Inventory[] gameInventory1 = game1.getAllInventories();
        assertEquals(4, gameInventory1[0].get(ORE));
        assertEquals(4, gameInventory1[0].get(WOOL));
        assertEquals(5, gameInventory1[0].get(BRICK));
        assertEquals(4, gameInventory1[0].get(GRAIN));
        assertEquals(5, gameInventory1[0].get(LUMBER));
    }

    /**
     * Tests if the gameManagement handles a BuyDevelopmentCardRequest properly when the
     * does not have enough resources to buy a development card
     * <p>
     * A BuyDevelopmentCardRequest is posted onto the event bus and the user
     * wants to buy a DevelopmentCard.
     * <p>
     * This test fails if the users gets a development card even if he had not enough resources
     */
    @Test
    void buyDevelopmentCardWithNotEnoughResourcesTest() {
        User[] user = new User[3];
        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
        ILobby lobby = new LobbyDTO(defaultLobby, user[0], null);
        lobby.joinUser(user[1]);
        lobby.joinUser(user[2]);
        IGameMapManagement gameMap = new GameMapManagement();
        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
        gameManagement.createGame(lobby, user[0], gameMap, 0);
        Game game = gameManagement.getGame(defaultLobby);
        Inventory[] gameInventory = game.getAllInventories();
        BankInventory bankInventory = game.getBankInventory();
        gameInventory[0].set(WOOL, 0);
        gameInventory[0].set(BRICK, 0);
        gameInventory[0].set(GRAIN, 0);
        gameInventory[0].set(ORE, 0);
        gameInventory[0].set(LUMBER, 0);
        assertEquals(0, gameInventory[0].get(WOOL));
        assertEquals(0, gameInventory[0].get(BRICK));
        assertEquals(0, gameInventory[0].get(ORE));
        assertEquals(0, gameInventory[0].get(GRAIN));
        assertEquals(0, gameInventory[0].get(LUMBER));

        assertEquals(0, gameInventory[0].get(KNIGHT_CARD));
        assertEquals(0, gameInventory[0].get(ROAD_BUILDING_CARD));
        assertEquals(0, gameInventory[0].get(MONOPOLY_CARD));
        assertEquals(0, gameInventory[0].get(YEAR_OF_PLENTY_CARD));
        assertEquals(0, gameInventory[0].get(VICTORY_POINT_CARD));

        Message buyDevelopmentCardRequest = new BuyDevelopmentCardRequest(user[0], defaultLobby);
        bus.post(buyDevelopmentCardRequest);

        Game game1 = gameManagement.getGame(defaultLobby);
        Inventory[] gameInventory1 = game1.getAllInventories();
        BankInventory bankInv = game1.getBankInventory();

        assertEquals(bankInventory, bankInv);
        assertEquals(0, gameInventory1[0].get(KNIGHT_CARD));
        assertEquals(0, gameInventory1[0].get(ROAD_BUILDING_CARD));
        assertEquals(0, gameInventory1[0].get(MONOPOLY_CARD));
        assertEquals(0, gameInventory1[0].get(YEAR_OF_PLENTY_CARD));
        assertEquals(0, gameInventory1[0].get(VICTORY_POINT_CARD));

        assertEquals(0, gameInventory1[0].get(WOOL));
        assertEquals(0, gameInventory1[0].get(BRICK));
        assertEquals(0, gameInventory1[0].get(ORE));
        assertEquals(0, gameInventory1[0].get(GRAIN));
        assertEquals(0, gameInventory1[0].get(LUMBER));
    }

    /**
     * Tests if the lobbyManagement handles a KickUserRequest properly when he wants to
     * kick himself
     * <p>
     * A KickUserRequest is posted onto the event bus and the owner
     * wants to kick himself
     * <p>
     * This test fails if he is able to kick himself
     */
    @Test
    void kickOwnerTest() {
        User[] user = new User[3];
        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
        loginUser(user[0]);
        loginUser(user[1]);
        loginUser(user[2]);
        lobbyManagement.createLobby(defaultLobby, user[0], "");
        Optional<ILobby> lobby = lobbyManagement.getLobby(defaultLobby);
        assertTrue(lobby.isPresent());
        lobby.get().joinUser(user[1]);
        lobby.get().joinUser(user[2]);
        //Owner tries to kick himself
        Message kickUser = new KickUserRequest(defaultLobby, user[0], user[0]);
        bus.post(kickUser);

        Optional<ILobby> lobby2 = lobbyManagement.getLobby(defaultLobby);
        assertTrue(lobby2.isPresent());
        assertEquals(3, lobby2.get().getUserOrDummies().size());
    }

    /**
     * Tests if the lobbyManagement handles a KickUserRequest properly
     * <p>
     * A KickUserRequest is posted onto the event bus and the owner
     * wants to kick another user.
     * <p>
     * This test fails if the other user does not get kicked
     */
    @Test
    void kickUserTest() {
        User[] user = new User[3];
        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
        loginUser(user[0]);
        loginUser(user[1]);
        loginUser(user[2]);
        lobbyManagement.createLobby(defaultLobby, user[0], "");
        Optional<ILobby> lobby = lobbyManagement.getLobby(defaultLobby);
        assertTrue(lobby.isPresent());
        lobby.get().joinUser(user[1]);
        lobby.get().joinUser(user[2]);

        Message kickUser = new KickUserRequest(defaultLobby, user[0], user[1]);
        bus.post(kickUser);

        Optional<ILobby> lobby2 = lobbyManagement.getLobby(defaultLobby);
        assertTrue(lobby2.isPresent());
        assertEquals(2, lobby2.get().getUserOrDummies().size());
    }

    /**
     * Tests if the lobbyManagement handles a KickUserRequest properly while a game is running
     * <p>
     * A KickUserRequest is posted onto the event bus and the owner
     * wants to kick another user.
     * <p>
     * This test fails if the other user gets kicked even if the game is running
     */
    @Test
    void kickUserWhileGameIsActive() {
        User[] user = new User[3];
        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
        lobbyManagement.createLobby(defaultLobby, user[0], "");
        Optional<ILobby> lobby = lobbyManagement.getLobby(defaultLobby);
        assertTrue(lobby.isPresent());
        lobby.get().joinUser(user[1]);
        lobby.get().joinUser(user[2]);
        IGameMapManagement gameMap = new GameMapManagement();
        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
        gameManagement.createGame(lobby.get(), user[0], gameMap, 0);

        Message kickUser = new KickUserRequest(defaultLobby, user[0], user[1]);
        bus.post(kickUser);

        Optional<ILobby> lobby2 = lobbyManagement.getLobby(defaultLobby);
        assertTrue(lobby2.isPresent());
        assertEquals(3, lobby2.get().getUserOrDummies().size());
    }

    /**
     * Tests if the lobbyManagement handles a KickUserRequest properly when the user who
     * wants to kick another user, is not the owner
     * <p>
     * A KickUserRequest is posted onto the event bus and the owner
     * wants to kick another user.
     * <p>
     * This test fails if the other user is able to kick another user
     */
    @Test
    void notOwnerKickOtherUser() {
        User[] user = new User[3];
        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
        lobbyManagement.createLobby(defaultLobby, user[0], "");
        Optional<ILobby> lobby = lobbyManagement.getLobby(defaultLobby);
        assertTrue(lobby.isPresent());
        lobby.get().joinUser(user[1]);
        lobby.get().joinUser(user[2]);
        //user[0] ist der owner, aber user[1] schickt die kick request
        Message kickUser = new KickUserRequest(defaultLobby, user[1], user[2]);
        bus.post(kickUser);

        Optional<ILobby> lobby2 = lobbyManagement.getLobby(defaultLobby);
        assertTrue(lobby2.isPresent());
        assertEquals(3, lobby2.get().getUserOrDummies().size());
    }

    @Test
    void onPlayKnightCardRequestTest() {
        User[] user = new User[3];
        user[0] = new UserDTO(0, "Johnny", "NailsGoSpin", "JoestarJohnny@jojo.jp");
        user[1] = new UserDTO(1, "Jolyne", "IloveDaddyJoJo", "CujohJolyne@jojo.jp");
        user[2] = new UserDTO(2, "Josuke", "4BallsBetterThan2", "HigashikataJosuke@jojo.jp");
        ILobby lobby = new LobbyDTO(defaultLobby, user[0], null);
        lobby.joinUser(user[1]);
        lobby.joinUser(user[2]);
        IGameMapManagement gameMap = new GameMapManagement();
        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
        gameManagement.createGame(lobby, user[0], gameMap, 0);
        Game game = gameManagement.getGame(lobby.getName());
        game.getInventory(Player.PLAYER_1).increase(KNIGHT_CARD, 1);
        game.setDiceRolledAlready(true);
        bus.post(new PlayKnightCardRequest(lobby.getName(), user[0]));
        assertEquals(1, game.getInventory(Player.PLAYER_1).getKnights());
    }

    @Test
    void onPlayMonopolyCardRequestTest() {
        User[] user = new User[3];
        user[0] = new UserDTO(0, "Johnny", "NailsGoSpin", "JoestarJohnny@jojo.jp");
        user[1] = new UserDTO(1, "Jolyne", "IloveDaddyJoJo", "CujohJolyne@jojo.jp");
        user[2] = new UserDTO(2, "Josuke", "4BallsBetterThan2", "HigashikataJosuke@jojo.jp");
        ILobby lobby = new LobbyDTO(defaultLobby, user[0], null);
        lobby.joinUser(user[1]);
        lobby.joinUser(user[2]);
        IGameMapManagement gameMap = new GameMapManagement();
        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
        gameManagement.createGame(lobby, user[0], gameMap, 0);
        Game game = gameManagement.getGame(lobby.getName());
        Inventory[] inventories = game.getAllInventories();
        inventories[1].increase(BRICK, 1);
        inventories[2].increase(BRICK, 2);
        inventories[0].increase(MONOPOLY_CARD, 1);
        game.setDiceRolledAlready(true);
        bus.post(new PlayMonopolyCardRequest(lobby.getName(), user[0], BRICK));
        assertEquals(3, inventories[0].get(BRICK));
        assertEquals(0, inventories[1].get(BRICK));
        assertEquals(0, inventories[2].get(BRICK));
    }

    @Test
    void onPlayYearOfPlentyCardRequestTest() {
        User[] user = new User[3];
        user[0] = new UserDTO(0, "Johnny", "NailsGoSpin", "JoestarJohnny@jojo.jp");
        user[1] = new UserDTO(1, "Jolyne", "IloveDaddyJoJo", "CujohJolyne@jojo.jp");
        user[2] = new UserDTO(2, "Josuke", "4BallsBetterThan2", "HigashikataJosuke@jojo.jp");
        ILobby lobby = new LobbyDTO(defaultLobby, user[0], null);
        lobby.joinUser(user[1]);
        lobby.joinUser(user[2]);
        IGameMapManagement gameMap = new GameMapManagement();
        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
        gameManagement.createGame(lobby, user[0], gameMap, 0);
        Game game = gameManagement.getGame(lobby.getName());
        assertEquals(0, game.getInventory(Player.PLAYER_1).get(BRICK));
        game.getInventory(Player.PLAYER_1).increase(YEAR_OF_PLENTY_CARD, 1);
        game.setDiceRolledAlready(true);
        bus.post(new PlayYearOfPlentyCardRequest(lobby.getName(), user[0], BRICK, GRAIN));
        assertEquals(1, game.getInventory(Player.PLAYER_1).get(BRICK));
        assertEquals(1, game.getInventory(Player.PLAYER_1).get(GRAIN));
    }

    @Test
    void onRoadBuildingCardRequestTest() {
        //Methode noch nicht fertig
    }

    @Test
    void testRobberMethods() {
        User[] user = new User[4];
        user[0] = new UserDTO(0, "Johnny", "NailsGoSpin", "JoestarJohnny@jojo.jp");
        user[1] = new UserDTO(1, "Jolyne", "IloveDaddyJoJo", "CujohJolyne@jojo.jp");
        user[2] = new UserDTO(2, "Josuke", "4BallsBetterThan2", "HigashikataJosuke@jojo.jp");
        UserOrDummy dummy = new DummyDTO();
        ILobby lobby = new LobbyDTO(defaultLobby, user[0], null);
        lobby.joinUser(user[1]);
        lobby.joinUser(user[2]);
        lobby.joinUser(dummy);
        IGameMapManagement gameMap = new GameMapManagement();
        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
        gameManagement.createGame(lobby, user[0], gameMap, 0);
        Game game = gameManagement.getGame(lobby.getName());
        game.setDiceRolledAlready(true);
        //Tests robbing a resource
        game.getInventory(user[1]).increase(BRICK, 1);
        game.getInventory(dummy).increase(ORE, 1);
        bus.post(new RobberChosenVictimRequest(lobby.getName(), user[0], user[1]));
        bus.post(new RobberChosenVictimRequest(lobby.getName(), user[0], dummy));
        assertEquals(1, game.getInventory(user[0]).get(BRICK));
        assertEquals(0, game.getInventory(user[1]).get(BRICK));
        assertEquals(1, game.getInventory(user[0]).get(ORE));
        assertEquals(0, game.getInventory(dummy).get(ORE));

        //Tests robberTax
        game.getInventory(user[2]).increase(ORE, 3);
        game.getInventory(user[2]).increase(GRAIN, 3);
        game.getInventory(user[2]).increase(WOOL, 4);
        ResourceList map = new ResourceList();
        map.set(ORE, 1);
        map.set(GRAIN, 2);
        map.set(WOOL, 2);
        bus.post(new RobberTaxChosenRequest(map, user[2], lobby.getName()));
        assertEquals(2, game.getInventory(user[2]).get(ORE));
        assertEquals(1, game.getInventory(user[2]).get(GRAIN));
        assertEquals(2, game.getInventory(user[2]).get(WOOL));

        //Tests new robber position
        MapPoint robPos = game.getMap().getRobberPosition();
        MapPoint mp = MapPoint.HexMapPoint(2, 4);
        bus.post(new RobberNewPositionChosenRequest(lobby.getName(), user[2], mp));
        assertNotEquals(robPos, game.getMap().getRobberPosition());
        assertEquals(mp, game.getMap().getRobberPosition());
    }

    /**
     * Tests if the gameManagement handles an ExecuteTradeWithBankRequest properly
     * <p>
     * An ExecuteTradeWithBankRequest is posted onto the event bus and the user
     * wants trade a resource with the bank.
     * <p>
     * This test fails if the users inventory is not updated properly or the User is able to
     * trade even if he has not enough resources.
     */
    @Test
    void tradeResourceWithBankTest() {
        User[] user = new User[3];
        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
        ILobby lobby = new LobbyDTO(defaultLobby, user[0], null);
        lobby.joinUser(user[1]);
        lobby.joinUser(user[2]);
        IGameMapManagement gameMap = new GameMapManagement();
        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
        gameManagement.createGame(lobby, user[0], gameMap, 0);
        Game game = gameManagement.getGame(defaultLobby);
        Inventory[] gameInventory = game.getAllInventories();
        for (ResourceType resource : ResourceType.values()) {
            gameInventory[0].increase(resource, 5);
        }
        assertEquals(5, gameInventory[0].get(WOOL));
        assertEquals(5, gameInventory[0].get(BRICK));
        assertEquals(5, gameInventory[0].get(ORE));
        assertEquals(5, gameInventory[0].get(GRAIN));
        assertEquals(5, gameInventory[0].get(LUMBER));
        game.setDiceRolledAlready(true);
        Message executeTradeWithBankRequest = new ExecuteTradeWithBankRequest(user[0], defaultLobby, WOOL, BRICK);

        bus.post(executeTradeWithBankRequest);
        Game game1 = gameManagement.getGame(defaultLobby);
        Inventory gameInventory1 = game1.getInventory(user[0]);
        assertEquals(5, gameInventory1.get(LUMBER));
        assertEquals(6, gameInventory1.get(WOOL));
        assertEquals(1, gameInventory1.get(BRICK));
        assertEquals(5, gameInventory1.get(GRAIN));
        assertEquals(5, gameInventory1.get(LUMBER));

        bus.post(executeTradeWithBankRequest);
        Game game2 = gameManagement.getGame(defaultLobby);
        //inventory doesnt change because user had not enough resources
        Inventory[] gameInventory2 = game2.getAllInventories();
        assertEquals(gameInventory1, gameInventory2[0]);
        assertEquals(5, gameInventory2[0].get(LUMBER));
        assertEquals(6, gameInventory2[0].get(WOOL));
        assertEquals(1, gameInventory2[0].get(BRICK));
        assertEquals(5, gameInventory2[0].get(GRAIN));
        assertEquals(5, gameInventory2[0].get(LUMBER));
    }

    /**
     * Tests if a AcceptUserTradeRequest is handled properly
     * <p>
     * A AcceptUserTradeRequest is posted onto the EventBus and the User wants
     * to trade with another User.
     * <p>
     * This test fails if the User´s inventories are not updated properly
     */
    @Test
    void tradeResourcesTest() {
        User[] user = new User[3];
        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
        loginUser(user[0]);
        loginUser(user[1]);
        loginUser(user[2]);

        ILobby lobby = new LobbyDTO(defaultLobby, user[0], null);
        lobby.joinUser(user[1]);
        lobby.joinUser(user[2]);
        IGameMapManagement gameMap = new GameMapManagement();
        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
        gameManagement.createGame(lobby, user[0], gameMap, 0);
        Game game = gameManagement.getGame(defaultLobby);
        Inventory[] gameInventory = game.getAllInventories();
        for (int i = 0; i <= 2; i++) {
            gameInventory[i].set(WOOL, 5);
            gameInventory[i].set(BRICK, 5);
            gameInventory[i].set(GRAIN, 5);
            gameInventory[i].set(ORE, 5);
            gameInventory[i].set(LUMBER, 5);
        }
        assertEquals(5, gameInventory[0].get(WOOL));
        assertEquals(5, gameInventory[0].get(BRICK));
        assertEquals(5, gameInventory[0].get(ORE));
        assertEquals(5, gameInventory[0].get(GRAIN));
        assertEquals(5, gameInventory[0].get(LUMBER));

        ResourceList offeredResourceList = new ResourceList();
        offeredResourceList.set(ORE, 3);
        offeredResourceList.set(BRICK, 2);

        ResourceList demandedResourceList = new ResourceList();
        demandedResourceList.set(WOOL, 1);
        demandedResourceList.set(LUMBER, 4);
        game.setDiceRolledAlready(true);
        Message tradeWithUser = new AcceptUserTradeRequest(user[1], user[0], defaultLobby,
                                                           demandedResourceList.create(), offeredResourceList.create());
        bus.post(tradeWithUser);

        Game game1 = gameManagement.getGame(defaultLobby);
        Inventory[] gameInventory1 = game1.getAllInventories();
        Inventory inventory0 = game1.getInventory(user[0]);
        Inventory inventory1 = game1.getInventory(user[1]);
        for (User i : user) {
            System.out.println(game1.getInventory(i).get(ORE));
        }
        assertEquals(2, inventory0.get(ORE));
        assertEquals(6, inventory0.get(WOOL));
        assertEquals(3, inventory0.get(BRICK));
        assertEquals(5, inventory0.get(GRAIN));
        assertEquals(9, inventory0.get(LUMBER));

        assertEquals(8, inventory1.get(ORE));
        assertEquals(4, inventory1.get(WOOL));
        assertEquals(7, inventory1.get(BRICK));
        assertEquals(5, inventory1.get(GRAIN));
        assertEquals(1, inventory1.get(LUMBER));
    }

    /**
     * Tests if a AcceptUserTradeRequest is handled properly
     * <p>
     * A AcceptUserTradeRequest is posted onto the EventBus and the User wants
     * to trade with another User even if there are not enough resources in one of
     * the inventories.
     * <p>
     * This test fails if the User´s inventories are not updated properly
     */
    @Test
    void tradeWithNotEnoughResources() {
        User[] user = new User[3];
        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
        loginUser(user[0]);
        loginUser(user[1]);
        loginUser(user[2]);

        ILobby lobby = new LobbyDTO(defaultLobby, user[0], null);
        lobby.joinUser(user[1]);
        lobby.joinUser(user[2]);
        IGameMapManagement gameMap = new GameMapManagement();
        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
        gameManagement.createGame(lobby, user[0], gameMap, 0);
        Game game = gameManagement.getGame(defaultLobby);
        Inventory[] gameInventory = game.getAllInventories();
        gameInventory[0].set(WOOL, 5);
        gameInventory[0].set(BRICK, 5);
        gameInventory[0].set(GRAIN, 5);
        gameInventory[0].set(ORE, 5);
        gameInventory[0].set(LUMBER, 5);

        gameInventory[2].set(WOOL, 0);
        gameInventory[2].set(BRICK, 0);
        gameInventory[2].set(GRAIN, 0);
        gameInventory[2].set(ORE, 0);
        gameInventory[2].set(LUMBER, 0);

        assertEquals(5, gameInventory[0].get(WOOL));
        assertEquals(5, gameInventory[0].get(BRICK));
        assertEquals(5, gameInventory[0].get(ORE));
        assertEquals(5, gameInventory[0].get(GRAIN));
        assertEquals(5, gameInventory[0].get(LUMBER));

        assertEquals(0, gameInventory[2].get(WOOL));
        assertEquals(0, gameInventory[2].get(BRICK));
        assertEquals(0, gameInventory[2].get(ORE));
        assertEquals(0, gameInventory[2].get(GRAIN));
        assertEquals(0, gameInventory[2].get(LUMBER));

        ResourceList offeredResourceList = new ResourceList();
        ResourceList demandedResourceList = new ResourceList();
        Message tradeWithUser = new AcceptUserTradeRequest(user[2], user[0], defaultLobby, demandedResourceList,
                                                           offeredResourceList);
        bus.post(tradeWithUser);

        Game game1 = gameManagement.getGame(defaultLobby);
        Inventory[] gameInventory1 = game1.getAllInventories();
        assertEquals(5, gameInventory1[0].get(WOOL));
        assertEquals(5, gameInventory1[0].get(BRICK));
        assertEquals(5, gameInventory1[0].get(ORE));
        assertEquals(5, gameInventory1[0].get(GRAIN));
        assertEquals(5, gameInventory1[0].get(LUMBER));

        assertEquals(0, gameInventory1[2].get(WOOL));
        assertEquals(0, gameInventory1[2].get(BRICK));
        assertEquals(0, gameInventory1[2].get(ORE));
        assertEquals(0, gameInventory1[2].get(GRAIN));
        assertEquals(0, gameInventory1[2].get(LUMBER));

        Message tradeWithUser2 = new AcceptUserTradeRequest(user[0], user[2], defaultLobby, demandedResourceList,
                                                            offeredResourceList);
        bus.post(tradeWithUser2);

        Game game2 = gameManagement.getGame(defaultLobby);
        Inventory[] gameInventory2 = game2.getAllInventories();
        assertEquals(5, gameInventory2[0].get(WOOL));
        assertEquals(5, gameInventory2[0].get(BRICK));
        assertEquals(5, gameInventory2[0].get(ORE));
        assertEquals(5, gameInventory2[0].get(GRAIN));
        assertEquals(5, gameInventory2[0].get(LUMBER));

        assertEquals(0, gameInventory2[2].get(WOOL));
        assertEquals(0, gameInventory2[2].get(BRICK));
        assertEquals(0, gameInventory2[2].get(ORE));
        assertEquals(0, gameInventory2[2].get(GRAIN));
        assertEquals(0, gameInventory2[2].get(LUMBER));
    }

    /**
     * Helper method to login users
     * <p>
     * This method resets the gameService and gameManagement variables to null
     */
    private void loginUser(User userToLogin) {
        userManagement.createUser(userToLogin);
        final Message loginRequest = new LoginRequest(userToLogin.getUsername(), userToLogin.getPassword());
        bus.post(loginRequest);

        assertTrue(userManagement.isLoggedIn(userToLogin));
        userManagement.dropUser(userToLogin);
    }
}
