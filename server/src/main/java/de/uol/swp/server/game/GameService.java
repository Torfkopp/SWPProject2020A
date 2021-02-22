package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.Inventory;
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
     * @param request The request found on the event bus
     *
     * @author Maximilian Lindner
     * @author Alwin Bossert
     * @since 2021-02-22
     */
    @Subscribe
    private void onBuyDevelopmentCardRequest(BuyDevelopmentCardRequest request) {
        if (LOG.isDebugEnabled()) LOG.debug("Received BuyDevelopmentCardRequest for Lobby " + request.getOriginLobby());
        Game game = gameManagement.getGame(request.getOriginLobby());
        List<String> bankInventory = game.getBankInventory();
        if (bankInventory != null && bankInventory.size() > 0) {
            Random zufall = new Random(); // neues Random Objekt, namens zufall
            int zufallsZahl = zufall.nextInt(bankInventory.size());
            String developmentCard = bankInventory.get(zufallsZahl);
            if (updatePlayersInventoryWithDevelopmentCard(developmentCard, request.getUser(),
                                                          request.getOriginLobby())) {
                bankInventory.remove(zufallsZahl);
                if (LOG.isDebugEnabled())
                    LOG.debug("Sending a BuyDevelopmentCard for Lobby " + request.getOriginLobby());
                AbstractResponseMessage returnMessage = new BuyDevelopmentCardResponse(request.getUser(),
                                                                                       request.getOriginLobby(),
                                                                                       developmentCard);
                if (request.getMessageContext().isPresent()) {
                    returnMessage.setMessageContext(request.getMessageContext().get());
                }
                post(returnMessage);
            } else LOG.debug("In the lobby " + request.getOriginLobby() + " the User " + request.getUser()
                                                                                                .getUsername() + "couldnt buy a development Card");
        }
    }

    /**
     * Handles a CreateGameMessage found on the EventBus
     * <p>
     * If a CreateGameMessage is detected on the Eventbus, this method is called.
     * It then requests the GameManagement to create a game.
     *
     * @param msg The CreateGameMessage
     *
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
     * Handles a TradeWithBankRequest found on the EventBus
     * <p>
     * It searches the inventories in the current game for the one that belongs
     * to the player sending the request. It then posts a InventoryForTradeResponse
     * that contains all the user's resources, saved in a resourceMap for
     * counted items (bricks, grain, etc.) .
     *
     * @param request The TradeWithBankRequest found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Alwin Bossert
     * @since 2021-02-21
     */
    @Subscribe
    private void onTradeWithBankRequest(TradeWithBankRequest request) {
        if (LOG.isDebugEnabled()) LOG.debug("Received TradeWithBankRequest for Lobby " + request.getName());
        Game game = gameManagement.getGame(request.getName());
        Inventory[] inventories = game.getInventories();
        Inventory inventory = null;
        for (Inventory value : inventories) {
            if (value.getPlayer().equals(request.getUser())) {
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

            AbstractResponseMessage returnMessage = new InventoryForTradeResponse(request.getUser(), request.getName(),
                                                                                  Collections.unmodifiableMap(
                                                                                          resourceMap));
            if (request.getMessageContext().isPresent()) {
                returnMessage.setMessageContext(request.getMessageContext().get());
            }
            post(returnMessage);
        }
    }

    /**
     * Handles a UpdateInventoryAfterTradeWithBankRequest found on the EventBus
     * <p>
     * If a UpdateInventoryAfterTradeWithBankRequest is found on the EventBus this method updates the inventory
     * of the player who traded with the bank. The resource he wants to trade gets -4
     * and the resource he wants gets +1. It then posts a TradeWithBankAcceptedResponse onto the EventBus.
     *
     * @param request The UpdateInventoryAfterTradeWithBankRequest found on the EventBus
     *
     * @author Alwin Bossert
     * @author Maximilian Lindner
     * @since 2021-02-21
     */
    @Subscribe
    private void onUpdateInventoryAfterTradeWithBankRequest(UpdateInventoryAfterTradeWithBankRequest request) {
        if (LOG.isDebugEnabled())
            LOG.debug("Received UpdateInventoryAfterTradeWithBankRequest for Lobby " + request.getOriginLobby());
        Game game = gameManagement.getGame(request.getOriginLobby());
        Inventory[] inventories = game.getInventories();
        Inventory inventory = null;
        for (Inventory value : inventories) {
            if (value.getPlayer().equals(request.getUser())) {
                if (value.getPlayer().equals(request.getUser())) {
                    inventory = value;
                    break;
                }
            }
        }
        if (inventory != null) {
            if (request.getGetResource().equals("ore")) inventory.setOre(inventory.getOre() + 1);
            if (request.getGetResource().equals("brick")) inventory.setBrick(inventory.getBrick() + 1);
            if (request.getGetResource().equals("grain")) inventory.setGrain(inventory.getGrain() + 1);
            if (request.getGetResource().equals("lumber")) inventory.setLumber(inventory.getLumber() + 1);
            if (request.getGetResource().equals("wool")) inventory.setWool(inventory.getWool() + 1);
            if (request.getGiveResource().equals("ore")) inventory.setOre(inventory.getOre() - 4);
            if (request.getGiveResource().equals("brick")) inventory.setBrick(inventory.getBrick() - 4);
            if (request.getGiveResource().equals("grain")) inventory.setGrain(inventory.getGrain() - 4);
            if (request.getGiveResource().equals("lumber")) inventory.setLumber(inventory.getLumber() - 4);
            if (request.getGiveResource().equals("wool")) inventory.setWool(inventory.getWool() - 4);
            System.out.println(
                    inventory.getOre() + "" + inventory.getGrain() + "" + inventory.getWool() + "" + inventory
                            .getLumber() + "" + inventory.getBrick());
            AbstractResponseMessage returnMessage = new TradeWithBankAcceptedResponse(request.getUser(),
                                                                                      request.getOriginLobby());
            if (request.getMessageContext().isPresent())
                returnMessage.setMessageContext(request.getMessageContext().get());
            post(returnMessage);
            LOG.debug("Sending a TradeWithBankAcceptedResponse to lobby" + request.getOriginLobby());
        }
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
            resourceMap.put("cards.knights", inventory.getKnightCards());
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
            if (req.getMessageContext().isPresent()) {
                returnMessage.setMessageContext(req.getMessageContext().get());
            }
            post(returnMessage);
        }
    }

    /**
     * Helper method
     * <p>
     * Adds the radom chosen development card and deletes the resources
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
        if (inventory != null) {
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
        } else return false;
    }
}