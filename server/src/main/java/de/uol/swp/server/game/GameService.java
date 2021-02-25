package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.Inventory;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.request.EndTurnRequest;
import de.uol.swp.common.game.request.UpdateInventoryRequest;
import de.uol.swp.common.lobby.response.UpdateInventoryResponse;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.lobby.LobbyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
     * Handles a CreateGameMessage found on the EventBus
     * <p>
     * If a CreateGameMessage is detected on the EventBus, this method is called.
     * It then requests the GameManagement to create a game.
     *
     * @param msg The CreateGameMessage found on the EventBus
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
     * Handles a MonopolyCardPlayedMessage found on the EventBus
     * <p>
     * If a MonopolyCardPlayedMessage is detected on the EventBus, this method is called.
     * It then requests the GameManagement to handle the card.
     *
     * @param msg The MonopolyCardPlayedMessage found on the EventBus
     * @see de.uol.swp.common.game.message.MonopolyCardPlayedMessage
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
     * @see de.uol.swp.common.game.message.RoadBuildingCardPlayedMessage
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
     * Handles a YearOfPlentyCardPlayedMessage found on the EventBus
     * <p>
     * If a YearOfPlentyCardPlayedMessage is detected on the EventBus, this method is called.
     * It then requests the GameManagement to handle the card.
     *
     * @param msg The YearOfPlentyCardPlayedMessage found on the EventBus
     * @see de.uol.swp.common.game.message.YearOfPlentyCardPlayedMessage
     * @since 2021-02-25
     */
    @Subscribe
    private void onYearOfPlentyCardPlayedMessage(YearOfPlentyCardPlayedMessage msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received YearOfPlentyCardPlayedMessage for Lobby " + msg.getLobbyName());
            LOG.debug("---- " + msg.getUser().getUsername() + "wants " + msg.getResource1() + " and " + msg.getResource2());
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
     * Handles a KnightCardPlayedMessage found on the EventBus
     * <p>
     * If a KnightCardPlayedMessage is detected on the EventBus, this method is called.
     * It then requests the GameManagement to handle the card.
     *
     * @param msg The KnightCardPlayedMessage found on the EventBus
     * @see de.uol.swp.common.game.message.KnightCardPlayedMessage
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
     * Handles a UpdateInventoryRequest found on the EventBus
     * <p>
     * It searches the inventories in the current game for the one that belongs
     * to the player sending the request. It then posts a UpdateInventoryResponse
     * that contains all the user's items, saved in a resourceMap for
     * counted items (bricks, grain, etc.) and a armyAndRoadMap which
     * contains the boolean attributes longestRoad and largestArmy.
     *
     * @param req The UpdateInventoryRequest found on the EventBus
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
                System.out.println(inventory.getPlayer());
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
}
