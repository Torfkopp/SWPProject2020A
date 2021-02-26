package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.Inventory;
import de.uol.swp.common.game.message.CardPlayedMessage.*;
import de.uol.swp.common.game.message.CreateGameMessage;
import de.uol.swp.common.game.message.NextPlayerMessage;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.response.*;
import de.uol.swp.common.lobby.request.TradeWithBankRequest;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.lobby.LobbyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Mapping EventBus calls to GameManagement calls
 *
 * @author Alwin Bossert
 * @author Mario Fokken
 * @author Marvin Drees
 * @see de.uol.swp.server.AbstractService
 * @since 2021-01-15
 */
@SuppressWarnings("UnstableApiUsage")
public class GameService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(GameService.class);

    private final IGameManagement gameManagement;
    private final LobbyService lobbyService;

    /**
     * Constructor
     *
     * @param bus            The EventBus used throughout the entire server (injected)
     * @param gameManagement The ChatManagement to use (injected)
     * @param lobbyService   The LobbyService to use (injected)
     *
     * @since 2021-01-15
     */
    @Inject
    public GameService(EventBus bus, IGameManagement gameManagement, LobbyService lobbyService) {
        super(bus);
        LOG.debug("GameService started");
        this.gameManagement = gameManagement;
        this.lobbyService = lobbyService;
    }

    /**
     * Handles a BuyDevelopmentCard found on the event bus
     * <p>
     * If a BuyDevelopmentCard is found on the event bus, this method checks
     * if there are development cards to sell available in the bankInventory.
     * If there is at least one card, a random card gets chosen and if the
     * user has enough resources, he gets the new card(happens in helper method).
     * Afterwards a new BuyDevelopmentCardResponse is posted onto the event bus.
     *
     * @param req The request found on the event bus
     *
     * @author Maximilian Lindner
     * @author Alwin Bossert
     * @see de.uol.swp.common.game.request.BuyDevelopmentCardRequest
     * @see de.uol.swp.common.game.response.BuyDevelopmentCardResponse
     * @since 2021-02-22
     */
    @Subscribe
    private void onBuyDevelopmentCardRequest(BuyDevelopmentCardRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received BuyDevelopmentCardRequest for Lobby " + req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        List<String> bankInventory = game.getBankInventory();
        if (bankInventory != null && bankInventory.size() > 0) {
            Random random = new Random(); // new Random object, named random
            int randInt = random.nextInt(bankInventory.size());
            String developmentCard = bankInventory.get(randInt);
            if (updatePlayersInventoryWithDevelopmentCard(developmentCard, req.getUser(), req.getOriginLobby())) {
                bankInventory.remove(randInt);
                if (LOG.isDebugEnabled()) LOG.debug("Sending a BuyDevelopmentCard for Lobby " + req.getOriginLobby());
                AbstractResponseMessage returnMessage = new BuyDevelopmentCardResponse(req.getUser(),
                                                                                       req.getOriginLobby(),
                                                                                       developmentCard);
                returnMessage.initWithMessage(req);
                post(returnMessage);
            } else LOG.debug("In the lobby " + req.getOriginLobby() + " the User " + req.getUser()
                                                                                        .getUsername() + "couldnt buy a development Card");
        }
    }

    /**
     * Handles a CreateGameMessage found on the EventBus
     * <p>
     * If a CreateGameMessage is detected on the EventBus, this method is called.
     * It then requests the GameManagement to create a game.
     *
     * @param msg The CreateGameMessage found on the EventBus
     *
     * @see de.uol.swp.common.game.message.CreateGameMessage
     * @since 2021-01-24
     */
    @Subscribe
    private void onCreateGameMessage(CreateGameMessage msg) {
        if (LOG.isDebugEnabled()) LOG.debug("Received CreateGameMessage for Lobby " + msg.getLobbyName());
        gameManagement.createGame(msg.getLobby(), msg.getFirst());
    }

    /**
     * Handles a EndTurnRequest found on the EventBus
     * <p>
     * If a EndTurnRequest is detected on the EventBus, this method is called.
     * It then requests the GameManagement to change to current active player.
     *
     * @param req The EndTurnRequest found on the EventBus
     *
     * @see de.uol.swp.common.game.request.EndTurnRequest
     * @see de.uol.swp.common.game.message.NextPlayerMessage
     * @since 2021-01-15
     */
    @Subscribe
    private void onEndTurnRequest(EndTurnRequest req) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received EndTurnRequest for Lobby " + req.getOriginLobby());
            LOG.debug("---- " + "User " + req.getUser().getUsername() + " wants to end his turn.");
        }
        try {
            Game game = gameManagement.getGame(req.getOriginLobby());
            ServerMessage returnMessage = new NextPlayerMessage(req.getOriginLobby(), game.nextPlayer());
            lobbyService.sendToAllInLobby(req.getOriginLobby(), returnMessage);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * Handles a KnightCardPlayedMessage found on the EventBus
     * <p>
     * If a KnightCardPlayedMessage is detected on the EventBus, this method is called.
     * It then requests the GameManagement to handle the card.
     *
     * @param msg The KnightCardPlayedMessage found on the EventBus
     *
     * @see de.uol.swp.common.game.message.CardPlayedMessage.KnightCardPlayedMessage
     * @since 2021-02-25
     */
    @Subscribe
    private void onKnightCardPlayedMessage(KnightCardPlayedMessage msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received KnightCardPlayedMessage for Lobby " + msg.getLobbyName());
            LOG.debug("---- " + msg.getUser().getUsername() + "wants to improve the army");
        }
        Game game = gameManagement.getGame(msg.getLobbyName());
        Inventory inv = game.getInventory(game.getPlayer(msg.getUser()));
        inv.setKnights(inv.getKnights() + 1);
    }

    /**
     * Handles a MonopolyCardPlayedMessage found on the EventBus
     * <p>
     * If a MonopolyCardPlayedMessage is detected on the EventBus, this method is called.
     * It then requests the GameManagement to handle the card.
     *
     * @param msg The MonopolyCardPlayedMessage found on the EventBus
     *
     * @see de.uol.swp.common.game.message.CardPlayedMessage.MonopolyCardPlayedMessage
     * @since 2021-02-25
     */
    @Subscribe
    private void onMonopolyCardPlayedMessage(MonopolyCardPlayedMessage msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received MonopolyCardPlayedMessage for Lobby " + msg.getLobbyName());
            LOG.debug("---- " + msg.getUser().getUsername() + "wants to monopolise " + msg.getResource());
        }
        Game game = gameManagement.getGame(msg.getLobbyName());
        Inventory invMono = game.getInventory(game.getPlayer(msg.getUser()));
        Inventory[] inventories = game.getInventories();
        int i = inventories.length;
        //Player gets one resource too much which gets reduced in the next step
        switch (msg.getResource()) {
            case ORE:
                invMono.increaseOre(i);
                for (Inventory inv : inventories) inv.increaseOre(-1);
            case WOOL:
                invMono.increaseWool(i);
                for (Inventory inv : inventories) inv.increaseWool(-1);
            case BRICK:
                invMono.increaseBrick(i);
                for (Inventory inv : inventories) inv.increaseBrick(-1);
            case GRAIN:
                invMono.increaseGrain(i);
                for (Inventory inv : inventories) inv.increaseGrain(-1);
            case LUMBER:
                invMono.increaseLumber(i);
                for (Inventory inv : inventories) inv.increaseLumber(-1);
        }
    }

    /**
     * Handles a RoadBuildingCardPlayedMessage found on the EventBus
     * <p>
     * If a RoadBuildingCardPlayedMessage is detected on the EventBus, this method is called.
     * It then requests the GameManagement to handle the card.
     *
     * @param msg The RoadBuildingCardPlayedMessage found on the EventBus
     *
     * @see de.uol.swp.common.game.message.CardPlayedMessage.RoadBuildingCardPlayedMessage
     * @since 2021-02-25
     */
    @Subscribe
    private void onRoadBuildingCardPlayedMessage(RoadBuildingCardPlayedMessage msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received RoadBuildingCardPlayedMessage for Lobby " + msg.getLobbyName());
            LOG.debug("---- " + msg.getUser().getUsername() + "wants to build a road");
        }
        //TODO: Implementierung
    }

    /**
     * Handles a TradeWithBankRequest found on the EventBus
     * <p>
     * It searches the inventories in the current game for the one that belongs
     * to the player sending the request. It then posts a InventoryForTradeResponse
     * that contains all the user's resources, saved in a resourceMap for
     * counted items (bricks, grain, etc.) .
     *
     * @param req The TradeWithBankRequest found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Alwin Bossert
     * @see de.uol.swp.common.lobby.request.TradeWithBankRequest
     * @see de.uol.swp.common.game.response.InventoryForTradeResponse
     * @since 2021-02-21
     */
    @Subscribe
    private void onTradeWithBankRequest(TradeWithBankRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received TradeWithBankRequest for Lobby " + req.getName());
        Game game = gameManagement.getGame(req.getName());
        Inventory[] inventories = game.getInventories();
        Inventory inventory = null;
        for (Inventory value : inventories) {
            if (value.getPlayer().equals(req.getUser())) {
                inventory = value;
                break;
            }
        }
        if (inventory != null) {
            Map<String, Integer> resourceMap = new HashMap<>();
            resourceMap.put("brick", inventory.getBrick());
            resourceMap.put("grain", inventory.getGrain());
            resourceMap.put("lumber", inventory.getLumber());
            resourceMap.put("ore", inventory.getOre());
            resourceMap.put("wool", inventory.getWool());

            AbstractResponseMessage returnMessage = new InventoryForTradeResponse(req.getUser(), req.getName(),
                                                                                  Collections.unmodifiableMap(
                                                                                          resourceMap));
            returnMessage.initWithMessage(req);
            post(returnMessage);
        }
    }

    /**
     * Handles a UpdateInventoryAfterTradeWithBankRequest found on the EventBus
     * <p>
     * If a UpdateInventoryAfterTradeWithBankRequest is found on the EventBus this method updates the inventory
     * of the player who traded with the bank. If the User has enough resources, the resource he wants to trade gets -4
     * and the resource he wants gets +1. It then posts a TradeWithBankAcceptedResponse onto the EventBus.
     *
     * @param req The UpdateInventoryAfterTradeWithBankRequest found on the EventBus
     *
     * @author Alwin Bossert
     * @author Maximilian Lindner
     * @see de.uol.swp.common.game.request.UpdateInventoryAfterTradeWithBankRequest
     * @see de.uol.swp.common.game.response.TradeWithBankAcceptedResponse
     * @since 2021-02-21
     */
    @Subscribe
    private void onUpdateInventoryAfterTradeWithBankRequest(UpdateInventoryAfterTradeWithBankRequest req) {
        if (LOG.isDebugEnabled())
            LOG.debug("Received UpdateInventoryAfterTradeWithBankRequest for Lobby " + req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory[] inventories = game.getInventories();
        Inventory inventory = null;
        for (Inventory value : inventories) {
            if (value.getPlayer().equals(req.getUser())) {
                if (value.getPlayer().equals(req.getUser())) {
                    inventory = value;
                    break;
                }
            }
        }
        if (inventory == null) return;

        if (req.getGiveResource().equals("ore") && (inventory.getOre() < 4)) return;
        if (req.getGiveResource().equals("brick") && (inventory.getBrick() < 4)) return;
        if (req.getGiveResource().equals("grain") && (inventory.getGrain() < 4)) return;
        if (req.getGiveResource().equals("lumber") && (inventory.getLumber() < 4)) return;
        if (req.getGiveResource().equals("wool") && (inventory.getWool() < 4)) return;

        if (req.getGetResource().equals("ore")) inventory.setOre(inventory.getOre() + 1);
        if (req.getGetResource().equals("brick")) inventory.setBrick(inventory.getBrick() + 1);
        if (req.getGetResource().equals("grain")) inventory.setGrain(inventory.getGrain() + 1);
        if (req.getGetResource().equals("lumber")) inventory.setLumber(inventory.getLumber() + 1);
        if (req.getGetResource().equals("wool")) inventory.setWool(inventory.getWool() + 1);

        if (req.getGiveResource().equals("ore")) inventory.setOre(inventory.getOre() - 4);
        if (req.getGiveResource().equals("brick")) inventory.setBrick(inventory.getBrick() - 4);
        if (req.getGiveResource().equals("grain")) inventory.setGrain(inventory.getGrain() - 4);
        if (req.getGiveResource().equals("lumber")) inventory.setLumber(inventory.getLumber() - 4);
        if (req.getGiveResource().equals("wool")) inventory.setWool(inventory.getWool() - 4);

        AbstractResponseMessage returnMessage = new TradeWithBankAcceptedResponse(req.getUser(), req.getOriginLobby());
        returnMessage.initWithMessage(req);
        post(returnMessage);
        LOG.debug("Sending a TradeWithBankAcceptedResponse to lobby" + req.getOriginLobby());
    }

    /**
     * Handles a UpdateInventoryRequest found on the EventBus
     * <p>
     * It searches the inventories in the current game for the one that belongs
     * to the player sending the request. It then posts a UpdateInventoryResponse
     * that contains all the user's items, saved in a resourceMap for
     * counted items (bricks, grain, etc.) and a armyAndRoadMap which
     * contains the boolean attributes longestRoad and largestArmy.
     *
     * @param req The UpdateInventoryRequest found on the EventBus
     *
     * @author Sven Ahrens
     * @author Finn Haase
     * @see de.uol.swp.common.game.request.UpdateInventoryRequest
     * @see de.uol.swp.common.game.response.UpdateInventoryResponse
     * @since 2021-01-25
     */
    @Subscribe
    private void onUpdateInventoryRequest(UpdateInventoryRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received UpdateInventoryRequest for Lobby " + req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory[] inventories = game.getInventories();
        Inventory inventory = null;
        for (Inventory value : inventories) {
            if (value.getPlayer().equals(req.getUser())) {
                inventory = value;
                break;
            }
        }
        if (inventory != null) {
            Map<String, Integer> resourceMap = new HashMap<>();
            resourceMap.put("brick", inventory.getBrick());
            resourceMap.put("grain", inventory.getGrain());
            resourceMap.put("lumber", inventory.getLumber());
            resourceMap.put("ore", inventory.getOre());
            resourceMap.put("wool", inventory.getWool());
            resourceMap.put("cards.victorypoints", inventory.getVictoryPointCards());
            resourceMap.put("cards.knight", inventory.getKnightCards());
            resourceMap.put("cards.roadbuilding", inventory.getRoadBuildingCards());
            resourceMap.put("cards.yearofplenty", inventory.getYearOfPlentyCards());
            resourceMap.put("cards.monopoly", inventory.getMonopolyCards());

            Map<String, Boolean> armyAndRoadMap = new HashMap<>();
            armyAndRoadMap.put("cards.unique.largestarmy", inventory.isLargestArmy());
            armyAndRoadMap.put("cards.unique.longestroad", inventory.isLongestRoad());

            AbstractResponseMessage returnMessage = new UpdateInventoryResponse(req.getUser(), req.getOriginLobby(),
                                                                                Collections
                                                                                        .unmodifiableMap(resourceMap),
                                                                                Collections.unmodifiableMap(
                                                                                        armyAndRoadMap));
            returnMessage.initWithMessage(req);
            post(returnMessage);
        }
    }

    /**
     * Handles a YearOfPlentyCardPlayedMessage found on the EventBus
     * <p>
     * If a YearOfPlentyCardPlayedMessage is detected on the EventBus, this method is called.
     * It then requests the GameManagement to handle the card.
     *
     * @param msg The YearOfPlentyCardPlayedMessage found on the EventBus
     *
     * @see de.uol.swp.common.game.message.CardPlayedMessage.YearOfPlentyCardPlayedMessage
     * @since 2021-02-25
     */
    @Subscribe
    private void onYearOfPlentyCardPlayedMessage(YearOfPlentyCardPlayedMessage msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received YearOfPlentyCardPlayedMessage for Lobby " + msg.getLobbyName());
            LOG.debug("---- " + msg.getUser().getUsername() + "wants " + msg.getResource1() + " and " + msg
                    .getResource2());
        }
        Game game = gameManagement.getGame(msg.getLobbyName());
        Inventory inv = game.getInventory(game.getPlayer(msg.getUser()));

        switch (msg.getResource1()) {
            case ORE:
                inv.increaseOre(1);
            case WOOL:
                inv.increaseWool(1);
            case BRICK:
                inv.increaseBrick(1);
            case GRAIN:
                inv.increaseGrain(1);
            case LUMBER:
                inv.increaseLumber(1);
        }

        switch (msg.getResource2()) {
            case ORE:
                inv.increaseOre(1);
            case WOOL:
                inv.increaseWool(1);
            case BRICK:
                inv.increaseBrick(1);
            case GRAIN:
                inv.increaseGrain(1);
            case LUMBER:
                inv.increaseLumber(1);
        }
    }

    /**
     * Helper method
     * <p>
     * Adds the random chosen development card and deletes the resources
     * he had to pay from his inventory.
     *
     * @param developmentCard Name of the random chosen development Card
     * @param user            User who wants to buy the development Card
     * @param lobbyName       Name of the lobby where the trade is happening
     *
     * @return a boolean if the trade worked out
     *
     * @author Maximilian Lindner
     * @author Alwin Bossert
     * @since 2021-02-22
     */
    private boolean updatePlayersInventoryWithDevelopmentCard(String developmentCard, User user, String lobbyName) {
        Game game = gameManagement.getGame(lobbyName);
        Inventory[] inventories = game.getInventories();
        Inventory inventory = null;
        for (Inventory value : inventories) {
            if (value.getPlayer().equals(user)) {
                inventory = value;
                break;
            }
        }
        if (inventory == null) return false;
        if (inventory.getOre() >= 1 && inventory.getGrain() >= 1 && inventory.getWool() >= 1) {

            inventory.setOre(inventory.getOre() - 1);
            inventory.setGrain(inventory.getGrain() - 1);
            inventory.setWool(inventory.getWool() - 1);
            if (developmentCard.equals("knightCard")) {
                inventory.setKnightCards(inventory.getKnightCards() + 1);
            }
            if (developmentCard.equals("roadBuildingCard")) {
                inventory.setRoadBuildingCards(inventory.getRoadBuildingCards() + 1);
            }
            if (developmentCard.equals("yearOfPlentyCard")) {
                inventory.setYearOfPlentyCards(inventory.getYearOfPlentyCards() + 1);
            }
            if (developmentCard.equals("monopolyCard")) {
                inventory.setMonopolyCards(inventory.getMonopolyCards() + 1);
            }
            if (developmentCard.equals("victoryPointCard")) {
                inventory.setVictoryPointCards(inventory.getVictoryPointCards() + 1);
            }
        }
        return true;
    }
}
