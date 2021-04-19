//package de.uol.swp.server.game;
//
//import com.google.common.eventbus.EventBus;
//import de.uol.swp.common.LobbyName;
//import de.uol.swp.common.game.*;
//import de.uol.swp.common.game.map.*;
//import de.uol.swp.common.game.request.AcceptUserTradeRequest;
//import de.uol.swp.common.game.request.BuyDevelopmentCardRequest;
//import de.uol.swp.common.game.request.ExecuteTradeWithBankRequest;
//import de.uol.swp.common.game.request.PlayCardRequest.PlayKnightCardRequest;
//import de.uol.swp.common.game.request.PlayCardRequest.PlayMonopolyCardRequest;
//import de.uol.swp.common.game.request.PlayCardRequest.PlayYearOfPlentyCardRequest;
//import de.uol.swp.common.game.robber.RobberChosenVictimRequest;
//import de.uol.swp.common.game.robber.RobberNewPositionChosenRequest;
//import de.uol.swp.common.game.robber.RobberTaxChosenRequest;
//import de.uol.swp.common.lobby.Lobby;
//import de.uol.swp.common.lobby.dto.LobbyDTO;
//import de.uol.swp.common.lobby.request.KickUserRequest;
//import de.uol.swp.common.message.Message;
//import de.uol.swp.common.user.*;
//import de.uol.swp.common.user.request.LoginRequest;
//import de.uol.swp.server.lobby.ILobbyManagement;
//import de.uol.swp.server.lobby.LobbyManagement;
//import de.uol.swp.server.lobby.LobbyService;
//import de.uol.swp.server.sessionmanagement.SessionManagement;
//import de.uol.swp.server.usermanagement.AuthenticationService;
//import de.uol.swp.server.usermanagement.UserManagement;
//import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
//import de.uol.swp.server.usermanagement.store.UserStore;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * This is a test of the class used to handle the requests sent by the client regarding the game
// *
// * @author Maximilian Lindner
// * @author Alwin Bossert
// * @since 2021-02-23
// */
//@SuppressWarnings("UnstableApiUsage")
//public class GameServiceTest {
//
//    private final EventBus bus = new EventBus();
//    private final UserStore userStore = new MainMemoryBasedUserStore();
//    private final UserManagement userManagement = new UserManagement(userStore);
//    private final ILobbyManagement lobbyManagement = new LobbyManagement();
//    private final SessionManagement sessionManagement = new SessionManagement();
//    private final LobbyService lobbyService = new LobbyService(lobbyManagement, sessionManagement, bus);
//    private final AuthenticationService authenticationService = new AuthenticationService(bus, userManagement,
//                                                                                          sessionManagement);
//    private IGameManagement gameManagement;
//    private GameService gameService;
//
//    /**
//     * Helper method run before each test case
//     * <p>
//     * This method instantiates a new GameManagement and a new GameService so that
//     * one test's Game objects don't interfere with another test's
//     */
//    @BeforeEach
//    protected void setUp() {
//        gameManagement = new GameManagement(lobbyManagement);
//        gameService = new GameService(bus, gameManagement, lobbyService);
//    }
//
//    /**
//     * Helper method run after each test case
//     * <p>
//     * This method resets the gameService and gameManagement variables to null
//     */
//    @AfterEach
//    protected void tearDown() {
//        gameService = null;
//        gameManagement = null;
//    }
//
//    /**
//     * Tests if the gameManagement handles a BuyDevelopmentCardRequest properly when the
//     * bankInventory is empty
//     * <p>
//     * A BuyDevelopmentCardRequest is posted onto the event bus and the user
//     * wants to buy a DevelopmentCard.
//     * <p>
//     * This test fails if the users gets a development card even if the banks inventory is empty
//     */
//    @Test
//    void BuyDevelopmentCardWhenBankInventoryIsEmptyTest() {
//        User[] user = new User[3];
//        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
//        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
//        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
//        Lobby lobby = new LobbyDTO(new LobbyName("testlobby"),  user[0], false, 4, false, 60, true, true);
//        lobby.joinUser(user[1]);
//        lobby.joinUser(user[2]);
//        IGameMapManagement gameMap = new GameMapManagement();
//        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
//        gameManagement.createGame(lobby, user[0], gameMap);
//        Game game = gameManagement.getGame(lobby.getName());
//        Inventory[] gameInventory = game.getAllInventories();
//        gameInventory[0].increase(Resource.ResourceType.WOOL, 5) ;
//        gameInventory[0].increase(Resource.ResourceType.BRICK, 5) ;
//        gameInventory[0].increase(Resource.ResourceType.GRAIN, 5) ;
//        gameInventory[0].increase(Resource.ResourceType.ORE, 5) ;
//        gameInventory[0].increase(Resource.ResourceType.LUMBER, 5) ;
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.WOOL));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.BRICK));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.ORE));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.GRAIN));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.LUMBER));
//
//        assertEquals(0, gameInventory[0].get(DevelopmentCard.DevelopmentCardType.KNIGHT_CARD));
//        assertEquals(0, gameInventory[0].get(DevelopmentCard.DevelopmentCardType.ROAD_BUILDING_CARD));
//        assertEquals(0, gameInventory[0].get(DevelopmentCard.DevelopmentCardType.MONOPOLY_CARD));
//        assertEquals(0, gameInventory[0].get(DevelopmentCard.DevelopmentCardType.YEAR_OF_PLENTY_CARD));
//        assertEquals(0, gameInventory[0].get(DevelopmentCard.DevelopmentCardType.VICTORY_POINT_CARD));
//
//        BankInventory bankInventory = game.getBankInventory();
//        //deletes the bank inventory
//        for (var developmentCard : bankInventory.getDevelopmentCards().entrySet()) {
//            bankInventory.decrease(developmentCard.getKey(), developmentCard.getValue());
//        }
//
//        Message buyDevelopmentCardRequest = new BuyDevelopmentCardRequest(user[0], new LobbyName("testlobby"));
//        bus.post(buyDevelopmentCardRequest);
//        Game game1 = gameManagement.getGame(new LobbyName("testlobby"));
//        Inventory[] gameInventory1 = game1.getAllInventories();
//        assertEquals(0, gameInventory1[0].get(DevelopmentCard.DevelopmentCardType.KNIGHT_CARD));
//        assertEquals(0, gameInventory1[0].get(DevelopmentCard.DevelopmentCardType.ROAD_BUILDING_CARD));
//        assertEquals(0, gameInventory1[0].get(DevelopmentCard.DevelopmentCardType.MONOPOLY_CARD));
//        assertEquals(0, gameInventory1[0].get(DevelopmentCard.DevelopmentCardType.YEAR_OF_PLENTY_CARD));
//        assertEquals(0, gameInventory1[0].get(DevelopmentCard.DevelopmentCardType.VICTORY_POINT_CARD));
//    }
//
//    /**
//     * Tests if the gameManagement handles a BuyDevelopmentCardRequest properly
//     * <p>
//     * A BuyDevelopmentCardRequest is posted onto the event bus and the user
//     * wants to buy a DevelopmentCard.
//     * <p>
//     * This test fails if the users inventory or the bank inventory are not
//     * updated properly.
//     */
//    @Test
//    void buyDevelopmentCardTest() {
//        User[] user = new User[3];
//        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
//        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
//        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
//        Lobby lobby = new LobbyDTO(new LobbyName("testlobby"),  user[0], false, 4, false, 60, true, true);
//        lobby.joinUser(user[1]);
//        lobby.joinUser(user[2]);
//        IGameMapManagement gameMap = new GameMapManagement();
//        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
//        gameManagement.createGame(lobby, user[0], gameMap);
//        Game game = gameManagement.getGame(new LobbyName("testlobby"));
//        Inventory[] gameInventory = game.getAllInventories();
//        gameInventory[0].increase(Resource.ResourceType.WOOL, 5) ;
//        gameInventory[0].increase(Resource.ResourceType.BRICK, 5) ;
//        gameInventory[0].increase(Resource.ResourceType.GRAIN, 5) ;
//        gameInventory[0].increase(Resource.ResourceType.ORE, 5) ;
//        gameInventory[0].increase(Resource.ResourceType.LUMBER, 5) ;
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.WOOL));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.BRICK));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.ORE));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.GRAIN));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.LUMBER));
//        int usersVictoryPointCards = gameInventory[0].get(DevelopmentCard.DevelopmentCardType.VICTORY_POINT_CARD);
//        int usersRoadBuildingCards = gameInventory[0].get(DevelopmentCard.DevelopmentCardType.ROAD_BUILDING_CARD) ;
//        int usersYearOfPlentyCards = gameInventory[0].get(DevelopmentCard.DevelopmentCardType.YEAR_OF_PLENTY_CARD);
//        int usersMonopolyCards = gameInventory[0].get(DevelopmentCard.DevelopmentCardType.MONOPOLY_CARD);
//        int usersKnightCards = gameInventory[0].get(DevelopmentCard.DevelopmentCardType.KNIGHT_CARD) ;
//        assertEquals(0, usersKnightCards);
//        assertEquals(0, usersRoadBuildingCards);
//        assertEquals(0, usersMonopolyCards);
//        assertEquals(0, usersYearOfPlentyCards);
//        assertEquals(0, usersVictoryPointCards);
//        //int knightCards = 0;
//        //int roadBuildingCards = 0;
//        //int yearOfPlentyCards = 0;
//        //int monopolyCards = 0;
//        //int victoryPointCards = 0;
//        //List<String> bankInventory = game.getBankInventory();
//        //for (String value : bankInventory) {
//        //    if (value.equals("game.resources.cards.knight")) knightCards++;
//        //    if (value.equals("game.resources.cards.roadbuilding")) roadBuildingCards++;
//        //    if (value.equals("game.resources.cards.yearofplenty")) yearOfPlentyCards++;
//        //    if (value.equals("game.resources.cards.monopoly")) monopolyCards++;
//        //    if (value.equals("game.resources.cards.victorypoints")) victoryPointCards++;
//        //}
//        Message buyDevelopmentCardRequest = new BuyDevelopmentCardRequest(user[0], "testlobby");
//        bus.post(buyDevelopmentCardRequest);
//        Game game1 = gameManagement.getGame("testlobby");
//        Inventory[] gameInventory1 = game1.getAllInventories();
//        assertEquals(4, gameInventory1[0].get(Resource.ResourceType.ORE));
//        assertEquals(4, gameInventory1[0].get(Resource.ResourceType.WOOL));
//        assertEquals(5, gameInventory1[0].get(Resource.ResourceType.BRICK));
//        assertEquals(4, gameInventory1[0].get(Resource.ResourceType.GRAIN));
//        assertEquals(5, gameInventory1[0].get(Resource.ResourceType.LUMBER));
//        //int newBankKnightCards = 0;
//        //int newBankRoadBuildingCards = 0;
//        //int newBankYearOfPlentyCards = 0;
//        //int newBankMonopolyCards = 0;
//        //int newBankVictoryPointCards = 0;
//        //List<String> newBankInventory = game1.getBankInventory();
//        //for (String s : newBankInventory) {
//        //    if (s.equals("game.resources.cards.knight")) newBankKnightCards++;
//        //    if (s.equals("game.resources.cards.roadbuilding")) newBankRoadBuildingCards++;
//        //    if (s.equals("game.resources.cards.yearofplenty")) newBankYearOfPlentyCards++;
//        //    if (s.equals("game.resources.cards.monopoly")) newBankMonopolyCards++;
//        //    if (s.equals("game.resources.cards.victorypoints")) newBankVictoryPointCards++;
//        //}
//        assertTrue(
//                ((newBankKnightCards == knightCards - 1) || (newBankMonopolyCards == monopolyCards - 1) || (newBankVictoryPointCards == victoryPointCards - 1) || (newBankYearOfPlentyCards == yearOfPlentyCards - 1) || (newBankRoadBuildingCards == roadBuildingCards - 1)));
//
//        int newKnightCards = gameInventory1[0].get(DevelopmentCard.DevelopmentCardType.KNIGHT_CARD) ;
//        int newRoadBuildingCards = gameInventory1[0].get(DevelopmentCard.DevelopmentCardType.ROAD_BUILDING_CARD) ;
//        int newYearOfPlentyCards = gameInventory1[0].get(DevelopmentCard.DevelopmentCardType.YEAR_OF_PLENTY_CARD);
//        int newMonopolyCards = gameInventory1[0].get(DevelopmentCard.DevelopmentCardType.MONOPOLY_CARD);
//        int newVictoryPointCards = gameInventory1[0].get(DevelopmentCard.DevelopmentCardType.VICTORY_POINT_CARD);
//        assertTrue(
//                ((usersKnightCards == newKnightCards - 1) || (usersMonopolyCards == newMonopolyCards - 1) || (usersVictoryPointCards == newVictoryPointCards - 1) || (usersYearOfPlentyCards == newYearOfPlentyCards - 1) || (usersRoadBuildingCards == newRoadBuildingCards - 1)));
//    }
//
//    /**
//     * Tests if the gameManagement handles a BuyDevelopmentCardRequest properly when the
//     * does not have enough resources to buy a development card
//     * <p>
//     * A BuyDevelopmentCardRequest is posted onto the event bus and the user
//     * wants to buy a DevelopmentCard.
//     * <p>
//     * This test fails if the users gets a development card even if he had not enough resources
//     */
//    @Test
//    void buyDevelopmentCardWithNotEnoughResourcesTest() {
//        User[] user = new User[3];
//        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
//        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
//        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
//        Lobby lobby = new LobbyDTO(new LobbyName("testlobby"),  user[0], false, 4, false, 60, true, true);
//        lobby.joinUser(user[1]);
//        lobby.joinUser(user[2]);
//        IGameMapManagement gameMap = new GameMapManagement();
//        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
//        gameManagement.createGame(lobby, user[0], gameMap);
//        Game game = gameManagement.getGame("testlobby");
//        Inventory[] gameInventory = game.getAllInventories();
//        List<String> bankInventory = game.getBankInventory();
//        gameInventory[0].setWool(0);
//        gameInventory[0].setBrick(0);
//        gameInventory[0].setGrain(0);
//        gameInventory[0].setOre(0);
//        gameInventory[0].setLumber(0);
//        assertEquals(0, gameInventory[0].get(Resource.ResourceType.WOOL));
//        assertEquals(0, gameInventory[0].get(Resource.ResourceType.BRICK));
//        assertEquals(0, gameInventory[0].get(Resource.ResourceType.ORE));
//        assertEquals(0, gameInventory[0].get(Resource.ResourceType.GRAIN));
//        assertEquals(0, gameInventory[0].get(Resource.ResourceType.LUMBER));
//
//        assertEquals(0, gameInventory[0].get(DevelopmentCard.DevelopmentCardType.KNIGHT_CARD));
//        assertEquals(0, gameInventory[0].get(DevelopmentCard.DevelopmentCardType.ROAD_BUILDING_CARD));
//        assertEquals(0, gameInventory[0].get(DevelopmentCard.DevelopmentCardType.MONOPOLY_CARD));
//        assertEquals(0, gameInventory[0].get(DevelopmentCard.DevelopmentCardType.YEAR_OF_PLENTY_CARD));
//        assertEquals(0, gameInventory[0].get(DevelopmentCard.DevelopmentCardType.VICTORY_POINT_CARD));
//
//        Message buyDevelopmentCardRequest = new BuyDevelopmentCardRequest(user[0], "testlobby");
//        bus.post(buyDevelopmentCardRequest);
//
//        Game game1 = gameManagement.getGame("testlobby");
//        Inventory[] gameInventory1 = game1.getAllInventories();
//        List<String> bankInv = game1.getBankInventory();
//
//        assertEquals(bankInventory, bankInv);
//        assertEquals(0, gameInventory1[0].get(DevelopmentCard.DevelopmentCardType.KNIGHT_CARD));
//        assertEquals(0, gameInventory1[0].get(DevelopmentCard.DevelopmentCardType.ROAD_BUILDING_CARD));
//        assertEquals(0, gameInventory1[0].get(DevelopmentCard.DevelopmentCardType.MONOPOLY_CARD));
//        assertEquals(0, gameInventory1[0].get(DevelopmentCard.DevelopmentCardType.YEAR_OF_PLENTY_CARD));
//        assertEquals(0, gameInventory1[0].get(DevelopmentCard.DevelopmentCardType.VICTORY_POINT_CARD));
//
//        assertEquals(0, gameInventory1[0].get(Resource.ResourceType.WOOL));
//        assertEquals(0, gameInventory1[0].get(Resource.ResourceType.BRICK));
//        assertEquals(0, gameInventory1[0].get(Resource.ResourceType.ORE));
//        assertEquals(0, gameInventory1[0].get(Resource.ResourceType.GRAIN));
//        assertEquals(0, gameInventory1[0].get(Resource.ResourceType.LUMBER));
//    }
//
//    /**
//     * Tests if the lobbyManagement handles a KickUserRequest properly when he wants to
//     * kick himself
//     * <p>
//     * A KickUserRequest is posted onto the event bus and the owner
//     * wants to kick himself
//     * <p>
//     * This test fails if he is able to kick himself
//     */
//    @Test
//    void kickOwnerTest() {
//        User[] user = new User[3];
//        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
//        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
//        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
//        loginUser(user[0]);
//        loginUser(user[1]);
//        loginUser(user[2]);
//        lobbyManagement.createLobby("testlobby", user[0], 4);
//        Optional<Lobby> lobby = lobbyManagement.getLobby("testlobby");
//        assertTrue(lobby.isPresent());
//        lobby.get().joinUser(user[1]);
//        lobby.get().joinUser(user[2]);
//        //Owner tries to kick himself
//        Message kickUser = new KickUserRequest("testlobby", user[0], user[0]);
//        bus.post(kickUser);
//
//        Optional<Lobby> lobby2 = lobbyManagement.getLobby("testlobby");
//        assertTrue(lobby2.isPresent());
//        assertEquals(3, lobby2.get().getUserOrDummies().size());
//    }
//
//    /**
//     * Tests if the lobbyManagement handles a KickUserRequest properly
//     * <p>
//     * A KickUserRequest is posted onto the event bus and the owner
//     * wants to kick another user.
//     * <p>
//     * This test fails if the other user does not get kicked
//     */
//    @Test
//    void kickUserTest() {
//        User[] user = new User[3];
//        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
//        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
//        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
//        loginUser(user[0]);
//        loginUser(user[1]);
//        loginUser(user[2]);
//        lobbyManagement.createLobby("testlobby", user[0], 4);
//        Optional<Lobby> lobby = lobbyManagement.getLobby("testlobby");
//        assertTrue(lobby.isPresent());
//        lobby.get().joinUser(user[1]);
//        lobby.get().joinUser(user[2]);
//
//        Message kickUser = new KickUserRequest("testlobby", user[0], user[1]);
//        bus.post(kickUser);
//
//        Optional<Lobby> lobby2 = lobbyManagement.getLobby("testlobby");
//        assertTrue(lobby2.isPresent());
//        assertEquals(2, lobby2.get().getUserOrDummies().size());
//    }
//
//    /**
//     * Tests if the lobbyManagement handles a KickUserRequest properly while a game is running
//     * <p>
//     * A KickUserRequest is posted onto the event bus and the owner
//     * wants to kick another user.
//     * <p>
//     * This test fails if the other user gets kicked even if the game is running
//     */
//    @Test
//    void kickUserWhileGameIsActive() {
//        User[] user = new User[3];
//        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
//        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
//        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
//        lobbyManagement.createLobby("testlobby", user[0], 4);
//        Optional<Lobby> lobby = lobbyManagement.getLobby("testlobby");
//        assertTrue(lobby.isPresent());
//        lobby.get().joinUser(user[1]);
//        lobby.get().joinUser(user[2]);
//        IGameMapManagement gameMap = new GameMapManagement();
//        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
//        gameManagement.createGame(lobby.get(), user[0], gameMap);
//
//        Message kickUser = new KickUserRequest("testlobby", user[0], user[1]);
//        bus.post(kickUser);
//
//        Optional<Lobby> lobby2 = lobbyManagement.getLobby("testlobby");
//        assertTrue(lobby2.isPresent());
//        assertEquals(3, lobby2.get().getUserOrDummies().size());
//    }
//
//    /**
//     * Tests if the lobbyManagement handles a KickUserRequest properly when the user who
//     * wants to kick another user, is not the owner
//     * <p>
//     * A KickUserRequest is posted onto the event bus and the owner
//     * wants to kick another user.
//     * <p>
//     * This test fails if the other user is able to kick another user
//     */
//    @Test
//    void notOwnerKickOtherUser() {
//        User[] user = new User[3];
//        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
//        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
//        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
//        lobbyManagement.createLobby("testlobby", user[0], 4);
//        Optional<Lobby> lobby = lobbyManagement.getLobby("testlobby");
//        assertTrue(lobby.isPresent());
//        lobby.get().joinUser(user[1]);
//        lobby.get().joinUser(user[2]);
//        //user[0] ist der owner, aber user[1] schickt die kick request
//        Message kickUser = new KickUserRequest("testlobby", user[1], user[2]);
//        bus.post(kickUser);
//
//        Optional<Lobby> lobby2 = lobbyManagement.getLobby("testlobby");
//        assertTrue(lobby2.isPresent());
//        assertEquals(3, lobby2.get().getUserOrDummies().size());
//    }
//
//    @Test
//    void onPlayKnightCardRequestTest() {
//        User[] user = new User[3];
//        user[0] = new UserDTO(0, "Johnny", "NailsGoSpin", "JoestarJohnny@jojo.jp");
//        user[1] = new UserDTO(1, "Jolyne", "IloveDaddyJoJo", "CujohJolyne@jojo.jp");
//        user[2] = new UserDTO(2, "Josuke", "4BallsBetterThan2", "HigashikataJosuke@jojo.jp");
//        Lobby lobby = new LobbyDTO("Read The Manga", user[0], true, 4, false, 60, true, true);
//        lobby.joinUser(user[1]);
//        lobby.joinUser(user[2]);
//        IGameMapManagement gameMap = new GameMapManagement();
//        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
//        gameManagement.createGame(lobby, user[0], gameMap);
//        Game game = gameManagement.getGame(lobby.getName());
//        game.getInventory(Player.PLAYER_1).increaseKnightCards(1);
//        bus.post(new PlayKnightCardRequest(lobby.getName(), user[0]));
//        assertEquals(1, game.getInventory(Player.PLAYER_1).getKnights());
//    }
//
//    @Test
//    void onPlayMonopolyCardRequestTest() {
//        User[] user = new User[3];
//        user[0] = new UserDTO(0, "Johnny", "NailsGoSpin", "JoestarJohnny@jojo.jp");
//        user[1] = new UserDTO(1, "Jolyne", "IloveDaddyJoJo", "CujohJolyne@jojo.jp");
//        user[2] = new UserDTO(2, "Josuke", "4BallsBetterThan2", "HigashikataJosuke@jojo.jp");
//        Lobby lobby = new LobbyDTO("Read The Manga", user[0], true, 4, false, 60, true, true);
//        lobby.joinUser(user[1]);
//        lobby.joinUser(user[2]);
//        IGameMapManagement gameMap = new GameMapManagement();
//        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
//        gameManagement.createGame(lobby, user[0], gameMap);
//        Game game = gameManagement.getGame(lobby.getName());
//        Inventory[] inventories = game.getAllInventories();
//        inventories[1].increaseBrick(1);
//        inventories[2].increaseBrick(2);
//        inventories[0].increaseMonopolyCards(1);
//        bus.post(new PlayMonopolyCardRequest(lobby.getName(), user[0], Resources.BRICK));
//        assertEquals(2, inventories[0].get(Resource.ResourceType.BRICK));
//        assertEquals(0, inventories[1].get(Resource.ResourceType.BRICK));
//        assertEquals(1, inventories[2].get(Resource.ResourceType.BRICK));
//    }
//
//    @Test
//    void onPlayYearOfPlentyCardRequestTest() {
//        User[] user = new User[3];
//        user[0] = new UserDTO(0, "Johnny", "NailsGoSpin", "JoestarJohnny@jojo.jp");
//        user[1] = new UserDTO(1, "Jolyne", "IloveDaddyJoJo", "CujohJolyne@jojo.jp");
//        user[2] = new UserDTO(2, "Josuke", "4BallsBetterThan2", "HigashikataJosuke@jojo.jp");
//        Lobby lobby = new LobbyDTO("Read The Manga", user[0], true, 4, false, 60, true, true);
//        lobby.joinUser(user[1]);
//        lobby.joinUser(user[2]);
//        IGameMapManagement gameMap = new GameMapManagement();
//        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
//        gameManagement.createGame(lobby, user[0], gameMap);
//        Game game = gameManagement.getGame(lobby.getName());
//        assertEquals(0, game.getInventory(Player.PLAYER_1).get(Resource.ResourceType.BRICK));
//        game.getInventory(Player.PLAYER_1).increaseYearOfPlentyCards(1);
//        bus.post(new PlayYearOfPlentyCardRequest(lobby.getName(), user[0], Resources.BRICK, Resources.GRAIN));
//        assertEquals(1, game.getInventory(Player.PLAYER_1).get(Resource.ResourceType.BRICK));
//        assertEquals(1, game.getInventory(Player.PLAYER_1).get(Resource.ResourceType.GRAIN));
//    }
//
//    @Test
//    void onRoadBuildingCardRequestTest() {
//        //Methode noch nicht fertig
//    }
//
//    @Test
//    void testRobberMethods() {
//        UserOrDummy[] user = new UserOrDummy[4];
//        user[0] = new UserDTO(0, "Johnny", "NailsGoSpin", "JoestarJohnny@jojo.jp");
//        user[1] = new UserDTO(1, "Jolyne", "IloveDaddyJoJo", "CujohJolyne@jojo.jp");
//        user[2] = new UserDTO(2, "Josuke", "4BallsBetterThan2", "HigashikataJosuke@jojo.jp");
//        user[3] = new DummyDTO();
//        Lobby lobby = new LobbyDTO("Read The Manga", user[0], true, 4, false, 60, true, true);
//        lobby.joinUser(user[1]);
//        lobby.joinUser(user[2]);
//        lobby.joinUser(user[3]);
//        IGameMapManagement gameMap = new GameMapManagement();
//        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
//        gameManagement.createGame(lobby, user[0], gameMap);
//        Game game = gameManagement.getGame(lobby.getName());
//
//        //Tests robbing a resource
//        game.getInventory(user[1]).increaseBrick(1);
//        game.getInventory(user[3]).increaseOre(1);
//        bus.post(new RobberChosenVictimRequest(lobby.getName(), (User) user[0], user[1]));
//        bus.post(new RobberChosenVictimRequest(lobby.getName(), (User) user[0], user[3]));
//        assertEquals(1, game.getInventory(user[0]).get(Resource.ResourceType.BRICK));
//        assertEquals(0, game.getInventory(user[1]).get(Resource.ResourceType.BRICK));
//        assertEquals(1, game.getInventory(user[0]).get(Resource.ResourceType.ORE));
//        assertEquals(0, game.getInventory(user[3]).get(Resource.ResourceType.ORE));
//
//        //Tests robberTax
//        game.getInventory(user[2]).increaseOre(3);
//        game.getInventory(user[2]).increaseGrain(3);
//        game.getInventory(user[2]).increaseWool(4);
//        Map<Resources, Integer> map = new HashMap<>();
//        map.put(Resources.ORE, 1);
//        map.put(Resources.GRAIN, 2);
//        map.put(Resources.WOOL, 2);
//        bus.post(new RobberTaxChosenRequest(map, (User) user[2], lobby.getName()));
//        assertEquals(2, game.getInventory(user[2]).get(Resource.ResourceType.ORE));
//        assertEquals(1, game.getInventory(user[2]).get(Resource.ResourceType.GRAIN));
//        assertEquals(2, game.getInventory(user[2]).get(Resource.ResourceType.WOOL));
//
//        //Tests new robber position
//        MapPoint robPos = game.getMap().getRobberPosition();
//        MapPoint mp = MapPoint.HexMapPoint(2, 4);
//        bus.post(new RobberNewPositionChosenRequest(lobby.getName(), (User) user[2], mp));
//        assertNotEquals(robPos, game.getMap().getRobberPosition());
//        assertEquals(mp, game.getMap().getRobberPosition());
//    }
//
//    /**
//     * Tests if the gameManagement handles an ExecuteTradeWithBankRequest properly
//     * <p>
//     * An ExecuteTradeWithBankRequest is posted onto the event bus and the user
//     * wants trade a resource with the bank.
//     * <p>
//     * This test fails if the users inventory is not updated properly or the User is able to
//     * trade even if he has not enough resources.
//     */
//    @Test
//    void tradeResourceWithBankTest() {
//        User[] user = new User[3];
//        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
//        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
//        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
//        Lobby lobby = new LobbyDTO(new LobbyName("testlobby"),  user[0], false, 4, false, 60, true, true);
//        lobby.joinUser(user[1]);
//        lobby.joinUser(user[2]);
//        IGameMapManagement gameMap = new GameMapManagement();
//        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
//        gameManagement.createGame(lobby, user[0], gameMap);
//        Game game = gameManagement.getGame("testlobby");
//        Inventory[] gameInventory = game.getAllInventories();
//        gameInventory[0].increase(Resource.ResourceType.WOOL, 5) ;
//        gameInventory[0].increase(Resource.ResourceType.BRICK, 5) ;
//        gameInventory[0].increase(Resource.ResourceType.GRAIN, 5) ;
//        gameInventory[0].increase(Resource.ResourceType.ORE, 5) ;
//        gameInventory[0].increase(Resource.ResourceType.LUMBER, 5) ;
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.WOOL));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.BRICK));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.ORE));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.GRAIN));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.LUMBER));
//
//        Message executeTradeWithBankRequest = new ExecuteTradeWithBankRequest(user[0], "testlobby",
//                                                                              "game.resources.wool",
//                                                                              "game.resources.brick");
//
//        bus.post(executeTradeWithBankRequest);
//        Game game1 = gameManagement.getGame("testlobby");
//        Inventory[] gameInventory1 = game1.getAllInventories();
//        assertEquals(5, gameInventory1[0].get(Resource.ResourceType.LUMBER));
//        assertEquals(6, gameInventory1[0].get(Resource.ResourceType.WOOL));
//        assertEquals(1, gameInventory1[0].get(Resource.ResourceType.BRICK));
//        assertEquals(5, gameInventory1[0].get(Resource.ResourceType.GRAIN));
//        assertEquals(5, gameInventory1[0].get(Resource.ResourceType.LUMBER));
//
//        bus.post(executeTradeWithBankRequest);
//        Game game2 = gameManagement.getGame("testlobby");
//        //inventory doesnt change because user had not enough resources
//        Inventory[] gameInventory2 = game2.getAllInventories();
//        assertEquals(gameInventory1[0], gameInventory2[0]);
//        assertEquals(5, gameInventory2[0].get(Resource.ResourceType.LUMBER));
//        assertEquals(6, gameInventory2[0].get(Resource.ResourceType.WOOL));
//        assertEquals(1, gameInventory2[0].get(Resource.ResourceType.BRICK));
//        assertEquals(5, gameInventory2[0].get(Resource.ResourceType.GRAIN));
//        assertEquals(5, gameInventory2[0].get(Resource.ResourceType.LUMBER));
//    }
//
//    /**
//     * Tests if a AcceptUserTradeRequest is handled properly
//     * <p>
//     * A AcceptUserTradeRequest is posted onto the EventBus and the User wants
//     * to trade with another User.
//     * <p>
//     * This test fails if the User´s inventories are not updated properly
//     */
//    @Test
//    void tradeResourcesTest() {
//        User[] user = new User[3];
//        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
//        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
//        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
//        loginUser(user[0]);
//        loginUser(user[1]);
//        loginUser(user[2]);
//
//        Lobby lobby = new LobbyDTO(new LobbyName("testlobby"),  user[0], false, 4, false, 60, true, true);
//        lobby.joinUser(user[1]);
//        lobby.joinUser(user[2]);
//        IGameMapManagement gameMap = new GameMapManagement();
//        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
//        gameManagement.createGame(lobby, user[0], gameMap);
//        Game game = gameManagement.getGame("testlobby");
//        Inventory[] gameInventory = game.getAllInventories();
//        for (int i = 0; i < 2; i++) {
//            gameInventory[i].increase(Resource.ResourceType.WOOL, 5) ;
//            gameInventory[i].increase(Resource.ResourceType.BRICK, 5) ;
//            gameInventory[i].increase(Resource.ResourceType.GRAIN, 5) ;
//            gameInventory[i].increase(Resource.ResourceType.ORE, 5) ;
//            gameInventory[i].increase(Resource.ResourceType.LUMBER, 5) ;
//        }
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.WOOL));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.BRICK));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.ORE));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.GRAIN));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.LUMBER));
//
//        Map<String, Integer> offeringResourceMap = new HashMap<>();
//        offeringResourceMap.put("game.resources.brick", 2);
//        offeringResourceMap.put("game.resources.ore", 3);
//        offeringResourceMap.put("game.resources.wool", 0);
//        offeringResourceMap.put("game.resources.grain", 0);
//        offeringResourceMap.put("game.resources.lumber", 0);
//        Map<String, Integer> respondingResourceMap = new HashMap<>();
//        respondingResourceMap.put("game.resources.brick", 0);
//        respondingResourceMap.put("game.resources.ore", 0);
//        respondingResourceMap.put("game.resources.wool", 1);
//        respondingResourceMap.put("game.resources.grain", 0);
//        respondingResourceMap.put("game.resources.lumber", 4);
//
//        Message tradeWithUser = new AcceptUserTradeRequest(user[1], user[0], "testlobby", respondingResourceMap,
//                                                           offeringResourceMap);
//        bus.post(tradeWithUser);
//
//        Game game1 = gameManagement.getGame("testlobby");
//        Inventory[] gameInventory1 = game1.getAllInventories();
//        assertEquals(2, gameInventory1[0].get(Resource.ResourceType.ORE));
//        assertEquals(6, gameInventory1[0].get(Resource.ResourceType.WOOL));
//        assertEquals(3, gameInventory1[0].get(Resource.ResourceType.BRICK));
//        assertEquals(5, gameInventory1[0].get(Resource.ResourceType.GRAIN));
//        assertEquals(9, gameInventory1[0].get(Resource.ResourceType.LUMBER));
//
//        assertEquals(8, gameInventory1[1].get(Resource.ResourceType.ORE));
//        assertEquals(4, gameInventory1[1].get(Resource.ResourceType.WOOL));
//        assertEquals(7, gameInventory1[1].get(Resource.ResourceType.BRICK));
//        assertEquals(5, gameInventory1[1].get(Resource.ResourceType.GRAIN));
//        assertEquals(1, gameInventory1[1].get(Resource.ResourceType.LUMBER));
//    }
//
//    /**
//     * Tests if a AcceptUserTradeRequest is handled properly
//     * <p>
//     * A AcceptUserTradeRequest is posted onto the EventBus and the User wants
//     * to trade with another User even if there are not enough resources in one of
//     * the inventories.
//     * <p>
//     * This test fails if the User´s inventories are not updated properly
//     */
//    @Test
//    void tradeWithNotEnoughResources() {
//        User[] user = new User[3];
//        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
//        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
//        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
//        loginUser(user[0]);
//        loginUser(user[1]);
//        loginUser(user[2]);
//
//        Lobby lobby = new LobbyDTO(new LobbyName("testlobby"), user[0], false, 4, false, 60, true, true);
//        lobby.joinUser(user[1]);
//        lobby.joinUser(user[2]);
//        IGameMapManagement gameMap = new GameMapManagement();
//        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
//        gameManagement.createGame(lobby, user[0], gameMap);
//        Game game = gameManagement.getGame("testlobby");
//        Inventory[] gameInventory = game.getAllInventories();
//        gameInventory[0].increase(Resource.ResourceType.WOOL, 5) ;
//        gameInventory[0].increase(Resource.ResourceType.BRICK, 5) ;
//        gameInventory[0].increase(Resource.ResourceType.GRAIN, 5) ;
//        gameInventory[0].increase(Resource.ResourceType.ORE, 5) ;
//        gameInventory[0].increase(Resource.ResourceType.LUMBER, 5) ;
//
//        gameInventory[2].setWool(0);
//        gameInventory[2].setBrick(0);
//        gameInventory[2].setGrain(0);
//        gameInventory[2].setOre(0);
//        gameInventory[2].setLumber(0);
//
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.WOOL));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.BRICK));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.ORE));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.GRAIN));
//        assertEquals(5, gameInventory[0].get(Resource.ResourceType.LUMBER));
//
//        assertEquals(0, gameInventory[2].get(Resource.ResourceType.WOOL));
//        assertEquals(0, gameInventory[2].get(Resource.ResourceType.BRICK));
//        assertEquals(0, gameInventory[2].get(Resource.ResourceType.ORE));
//        assertEquals(0, gameInventory[2].get(Resource.ResourceType.GRAIN));
//        assertEquals(0, gameInventory[2].get(Resource.ResourceType.LUMBER));
//
//        Map<String, Integer> offeringResourceMap = new HashMap<>();
//        offeringResourceMap.put("brick", 2);
//        offeringResourceMap.put("ore", 3);
//        offeringResourceMap.put("wool", 0);
//        offeringResourceMap.put("grain", 0);
//        offeringResourceMap.put("lumber", 0);
//        Map<String, Integer> respondingResourceMap = new HashMap<>();
//        respondingResourceMap.put("wool", 1);
//        respondingResourceMap.put("lumber", 4);
//        respondingResourceMap.put("brick", 0);
//        respondingResourceMap.put("ore", 0);
//        respondingResourceMap.put("grain", 0);
//
//        Message tradeWithUser = new AcceptUserTradeRequest(user[2], user[0], "testlobby", respondingResourceMap,
//                                                           offeringResourceMap);
//        bus.post(tradeWithUser);
//
//        Game game1 = gameManagement.getGame("testlobby");
//        Inventory[] gameInventory1 = game1.getAllInventories();
//        assertEquals(5, gameInventory1[0].get(Resource.ResourceType.WOOL));
//        assertEquals(5, gameInventory1[0].get(Resource.ResourceType.BRICK));
//        assertEquals(5, gameInventory1[0].get(Resource.ResourceType.ORE));
//        assertEquals(5, gameInventory1[0].get(Resource.ResourceType.GRAIN));
//        assertEquals(5, gameInventory1[0].get(Resource.ResourceType.LUMBER));
//
//        assertEquals(0, gameInventory1[2].get(Resource.ResourceType.WOOL));
//        assertEquals(0, gameInventory1[2].get(Resource.ResourceType.BRICK));
//        assertEquals(0, gameInventory1[2].get(Resource.ResourceType.ORE));
//        assertEquals(0, gameInventory1[2].get(Resource.ResourceType.GRAIN));
//        assertEquals(0, gameInventory1[2].get(Resource.ResourceType.LUMBER));
//
//        Message tradeWithUser2 = new AcceptUserTradeRequest(user[0], user[2], "testlobby", respondingResourceMap,
//                                                            offeringResourceMap);
//        bus.post(tradeWithUser2);
//
//        Game game2 = gameManagement.getGame("testlobby");
//        Inventory[] gameInventory2 = game2.getAllInventories();
//        assertEquals(5, gameInventory2[0].get(Resource.ResourceType.WOOL));
//        assertEquals(5, gameInventory2[0].get(Resource.ResourceType.BRICK));
//        assertEquals(5, gameInventory2[0].get(Resource.ResourceType.ORE));
//        assertEquals(5, gameInventory2[0].get(Resource.ResourceType.GRAIN));
//        assertEquals(5, gameInventory2[0].get(Resource.ResourceType.LUMBER));
//
//        assertEquals(0, gameInventory2[2].get(Resource.ResourceType.WOOL));
//        assertEquals(0, gameInventory2[2].get(Resource.ResourceType.BRICK));
//        assertEquals(0, gameInventory2[2].get(Resource.ResourceType.ORE));
//        assertEquals(0, gameInventory2[2].get(Resource.ResourceType.GRAIN));
//        assertEquals(0, gameInventory2[2].get(Resource.ResourceType.LUMBER));
//    }
//
//    /**
//     * Helper method to login users
//     * <p>
//     * This method resets the gameService and gameManagement variables to null
//     */
//    private void loginUser(User userToLogin) {
//        userManagement.createUser(userToLogin);
//        final Message loginRequest = new LoginRequest(userToLogin.getUsername(), userToLogin.getPassword());
//        bus.post(loginRequest);
//
//        assertTrue(userManagement.isLoggedIn(userToLogin));
//        userManagement.dropUser(userToLogin);
//    }
//}
