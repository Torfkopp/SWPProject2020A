package de.uol.swp.client.trade;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.game.IGameService;
import de.uol.swp.client.trade.event.TradeUpdateEvent;
import de.uol.swp.common.game.map.Hexes.IHarborHex;
import de.uol.swp.common.game.response.BuyDevelopmentCardResponse;
import de.uol.swp.common.game.response.InventoryForTradeResponse;
import de.uol.swp.common.game.response.TradeWithBankAcceptedResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Window;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the TradingWithBank window
 *
 * @author Alwin Bossert
 * @author Maximilian Lindner
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-02-19
 */
@SuppressWarnings("UnstableApiUsage")
public class TradeWithBankPresenter extends AbstractTradePresenter {

    public static final String fxml = "/fxml/TradeWithBankView.fxml";
    public static final int MIN_HEIGHT = 433;
    public static final int MIN_WIDTH = 620;
    private final Logger LOG = LogManager.getLogger(TradeWithBankPresenter.class);
    private String lobbyName;
    private Map<String, Integer> resourceMap;
    private List<IHarborHex.HarborResource> harborMap;
    private ObservableList<Pair<String, Integer>> resourceList;
    private ObservableList<Pair<String, Integer>> bankResourceList;
    private ObservableList<Pair<String, Integer>> ownInventoryList;

    @Inject
    private IGameService gameService;
    @Inject
    private ITradeService tradeService;

    @FXML
    private ListView<Pair<String, Integer>> ownInventoryView;
    @FXML
    private ListView<Pair<String, Integer>> ownResourceToTradeWithView;
    @FXML
    private ListView<Pair<String, Integer>> bankResourceView;
    @FXML
    private Button buyDevelopmentButton;
    @FXML
    private Button tradeResourceWithBankButton;

    /**
     * Constructor
     * <p>
     * Sets the eventBus
     *
     * @param eventBus EventBus
     */
    @Inject
    public TradeWithBankPresenter(EventBus eventBus) {
        setEventBus(eventBus);
    }

    /**
     * Initialises the Presenter by setting up the ownResourceView and
     * the bankResourceView.
     *
     * @implNote Called automatically by JavaFX
     */
    @FXML
    public void initialize() {
        super.initialize();
        ownResourceToTradeWithView.setCellFactory(lv -> getListCell());
        bankResourceView.setCellFactory(lv -> getListCell());
        LOG.debug("TradeWithBankPresenter initialised");
    }

    /**
     * Handles a click on the Buy Button
     * <p>
     * Method called when the BuyBankButton is pressed.
     * The Method posts a BuyBankRequest including logged in user
     * onto the EventBus.
     *
     * @see de.uol.swp.common.game.request.BuyDevelopmentCardRequest
     */
    @FXML
    private void onBuyDevelopmentCardButtonPressed() {
        //@formatter:off
        if (resourceMap.get("game.resources.ore") >= 1 &&
            resourceMap.get("game.resources.grain") >= 1 &&
            resourceMap.get("game.resources.wool") >= 1) {
            tradeService.buyDevelopmentCard(lobbyName);
        }
        //@formatter:on
    }

    /**
     * If a BuyDevelopmentCardResponse is found on the event bus,
     * this method calls the close method, which closes the trading
     * window and posts a updateInventoryRequest onto the EventBus
     * to get the new Inventory after the trade shown in the
     * LobbyView.
     *
     * @param rsp The BuyDevelopmentCardResponse found on the eventBus
     *
     * @see de.uol.swp.common.game.response.BuyDevelopmentCardResponse
     * @see de.uol.swp.common.game.request.UpdateInventoryRequest
     */
    @Subscribe
    private void onBuyDevelopmentCardResponse(BuyDevelopmentCardResponse rsp) {
        if (!lobbyName.equals(rsp.getLobbyName())) return;
        LOG.debug("Received BuyDevelopmentCardResponse for Lobby " + lobbyName);
        LOG.debug("---- The user got a " + rsp.getDevelopmentCard());
        tradeService.closeBankTradeWindow(lobbyName);
        gameService.updateInventory(lobbyName);
        tradeResourceWithBankButton.setDisable(true);
    }

    /**
     * Handles a click on the Cancel Button
     * <p>
     * Method called when the CancelButton is pressed and uses
     * the helperFunction closeWindow to close the window
     * properly.
     */
    @FXML
    private void onCancelButtonPressed() {
        tradeService.closeBankTradeWindow(lobbyName);
    }

    /**
     * Handles a InventoryForTradeResponse found on the eventBus
     * <p>
     * If the InventoryForTradeResponse is directed to this lobby,
     * the TradeWithBankPresenter gets the inventory of the player
     * as a Map. Calls a function to fill the inventory.
     * If the user has enough resources, the buy the buyDevelopmentButton
     * gets enabled.
     *
     * @param rsp InventoryForTradeResponse having the inventory
     *
     * @see de.uol.swp.common.game.response.InventoryForTradeResponse
     */
    @Subscribe
    private void onInventoryForTradeResponse(InventoryForTradeResponse rsp) {
        if (rsp.getLobbyName().equals(this.lobbyName)) {
            LOG.debug("Received InventoryForTradeResponse for Lobby " + this.lobbyName);
            harborMap = rsp.getHarborResourceList();
            resourceMap = rsp.getResourceMap();
            setTradingLists();
        }
        if (resourceMap.get("game.resources.ore") >= 1 && resourceMap.get("game.resources.grain") >= 1 && resourceMap
                                                                                                                  .get("game.resources.wool") >= 1) {
            buyDevelopmentButton.setDisable(false);
        }
    }

    /**
     * Handles a click on the Trade Button
     * <p>
     * Method called when the TradeBankButton is pressed.
     * This method checks both lists for the selected item.
     * If there is a selected item in both lists, it posts an
     * ExecuteTradeWithBankRequest onto the EventBus.
     *
     * @see de.uol.swp.client.trade.event.TradeErrorEvent
     * @see de.uol.swp.common.game.request.ExecuteTradeWithBankRequest
     */
    @FXML
    private void onTradeResourceWithBankButtonPressed() {
        Pair<String, Integer> bankResource;
        Pair<String, Integer> giveResource;
        ownResourceToTradeWithView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        if (ownResourceToTradeWithView.getSelectionModel().isEmpty()) {
            tradeService.showTradeError(resourceBundle.getString("game.error.trade.noplayerresource"));
            return;
        } else {
            giveResource = ownResourceToTradeWithView.getSelectionModel().getSelectedItem();
        }
        bankResourceView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        if (bankResourceView.getSelectionModel().isEmpty()) {
            tradeService.showTradeError(resourceBundle.getString("game.error.trade.nobankresource"));
            return;
        } else {
            bankResource = bankResourceView.getSelectionModel().getSelectedItem();
        }
        if (bankResource != null && giveResource != null) {
            String userGetsResource = bankResource.getKey();
            String userLosesResource = giveResource.getKey();
            if (userGetsResource.equals(userLosesResource)) return;
            tradeService.executeTradeWithBank(lobbyName, userGetsResource, userLosesResource);
        }
    }

    /**
     * Handles a TradeUpdateEvent
     * <p>
     * If the lobbyName and the logged in user of the TradeWithBankPresenter are
     * null, they get the parameters of the event. This Event is sent when a new
     * TradeWithBankPresenter is created. If a window is closed using the
     * X(top-right-Button), the closeWindow method is called.
     *
     * @param event TradeUpdateEvent found on the event bus
     *
     * @see de.uol.swp.client.trade.event.TradeUpdateEvent
     */
    @Subscribe
    private void onTradeUpdateEvent(TradeUpdateEvent event) {
        if (lobbyName == null) lobbyName = event.getLobbyName();
        LOG.debug("Received TradeUpdateEvent for Lobby " + this.lobbyName);
        Window window = ownResourceToTradeWithView.getScene().getWindow();
        window.setOnCloseRequest(windowEvent -> tradeService.closeBankTradeWindow(lobbyName));
    }

    /**
     * If a TradeWithBankAcceptedResponse is found on the EventBus,
     * this method calls the close method, which closes the trading
     * window and posts a updateInventoryRequest onto the EventBus
     * to get the new Inventory after the trade shown in the
     * LobbyView.
     *
     * @param rsp TradeWithBankButtonAcceptedResponse found on the EventBus
     */
    @Subscribe
    private void onTradeWithBankAcceptedResponse(TradeWithBankAcceptedResponse rsp) {
        if (!lobbyName.equals(rsp.getLobbyName())) return;
        LOG.debug("Received TradeWithBankAcceptedResponse for Lobby " + this.lobbyName);
        tradeService.closeBankTradeWindow(lobbyName);
        gameService.updateInventory(lobbyName);
    }

    /**
     * Helper Function
     * <p>
     * If there is no resourceList it gets created and cleared. Then it gets
     * updated with the items as listed in the resourceMap.
     * The same happens for the ownInventoryList and the bankResourceList.
     */
    private void setTradingLists() {
        Map<IHarborHex.HarborResource, Integer> tradingRatio = new HashMap<>();
        if (resourceList == null) {
            resourceList = FXCollections.observableArrayList();
            ownInventoryList = FXCollections.observableArrayList();
            ownResourceToTradeWithView.setItems(resourceList);
            ownInventoryView.setItems(ownInventoryList);
        }
        resourceList.clear();
        ownInventoryList.clear();
        int prepareTradingRatio = 4;
        if (harborMap.contains(IHarborHex.HarborResource.ANY)) prepareTradingRatio = 3;
        tradingRatio.put(IHarborHex.HarborResource.BRICK, prepareTradingRatio);
        tradingRatio.put(IHarborHex.HarborResource.ORE, prepareTradingRatio);
        tradingRatio.put(IHarborHex.HarborResource.GRAIN, prepareTradingRatio);
        tradingRatio.put(IHarborHex.HarborResource.WOOL, prepareTradingRatio);
        tradingRatio.put(IHarborHex.HarborResource.LUMBER, prepareTradingRatio);
        if (harborMap.contains(IHarborHex.HarborResource.BRICK))
            tradingRatio.replace(IHarborHex.HarborResource.BRICK, 2);
        if (harborMap.contains(IHarborHex.HarborResource.ORE)) tradingRatio.replace(IHarborHex.HarborResource.ORE, 2);
        if (harborMap.contains(IHarborHex.HarborResource.GRAIN))
            tradingRatio.replace(IHarborHex.HarborResource.GRAIN, 2);
        if (harborMap.contains(IHarborHex.HarborResource.WOOL)) tradingRatio.replace(IHarborHex.HarborResource.WOOL, 2);
        if (harborMap.contains(IHarborHex.HarborResource.LUMBER))
            tradingRatio.replace(IHarborHex.HarborResource.LUMBER, 2);

        for (Map.Entry<String, Integer> entry : resourceMap.entrySet()) {
            ownInventoryList.add(new Pair<>(entry.getKey(), entry.getValue()));
            String resource = entry.getKey().toUpperCase();
            resource = resource.replaceFirst("^GAME.RESOURCES.", "");
            IHarborHex.HarborResource harborResource = IHarborHex.HarborResource.valueOf(resource);
            if (entry.getValue() < tradingRatio.get(harborResource)) continue;
            resourceList.add(new Pair<>(entry.getKey(), tradingRatio.get(harborResource)));
        }
        if (resourceList.size() == 0) {
            tradeResourceWithBankButton.setDisable(true);
        }
        if (bankResourceList == null) {
            bankResourceList = FXCollections.observableArrayList();
            bankResourceView.setItems(bankResourceList);
        }
        bankResourceList.clear();
        for (Map.Entry<String, Integer> entry : resourceMap.entrySet()) {
            bankResourceList.add(new Pair<>(entry.getKey(), 1));
        }
    }
}
