package de.uol.swp.client.trade;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.game.IGameService;
import de.uol.swp.client.trade.event.TradeUpdateEvent;
import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.game.map.Hexes.IHarborHex;
import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.game.response.BuyDevelopmentCardResponse;
import de.uol.swp.common.game.response.InventoryForTradeResponse;
import de.uol.swp.common.game.response.TradeWithBankAcceptedResponse;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Window;
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
@SuppressWarnings({"UnstableApiUsage", "rawtypes"})
public class TradeWithBankPresenter extends AbstractTradePresenter {

    public static final String fxml = "/fxml/TradeWithBankView.fxml";
    public static final int MIN_HEIGHT = 433;
    public static final int MIN_WIDTH = 620;
    private static final Logger LOG = LogManager.getLogger(TradeWithBankPresenter.class);
    private String lobbyName;

    @Inject
    private IGameService gameService;

    @FXML
    private TableView<Map<String, Object>> ownResourcesToTradeWith;
    @FXML
    private TableView<Map<String, Object>> bankResourcesView;
    // MapValueFactory doesn't support specifying a Map's generics, so the Map type is used raw here (Warning suppressed)
    @FXML
    private TableColumn<Map, Integer> tradeResourceAmountCol;
    @FXML
    private TableColumn<Map, String> tradeResourceNameCol;
    @FXML
    private TableColumn<Map, Integer> bankResourceAmountCol;
    @FXML
    private TableColumn<Map, String> bankResourceNameCol;
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
     * Helper method to set the tradingRatio Map according to the provided harborMap
     * <p>
     * This method checks for which harbors the User owns and sets the entry in the
     * tradingRation Map accordingly.
     *
     * @param harborMap Map of HarborResource the User has access to
     *
     * @return Map of HarborResource to trading ratio
     *
     * @author Phillip-André Suhr
     * @since 2021-04-20
     */
    private static Map<IHarborHex.HarborResource, Integer> setupHarborRatios(
            List<IHarborHex.HarborResource> harborMap) {
        Map<IHarborHex.HarborResource, Integer> tradingRatio = new HashMap<>();
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
        return tradingRatio;
    }

    /**
     * Initialises the Presenter by setting up the MapValueFactories for
     * the Bank and Trading inventories.
     *
     * @implNote Called automatically by JavaFX
     */
    @FXML
    public void initialize() {
        super.initialize();
        tradeResourceAmountCol.setCellValueFactory(new MapValueFactory<>("amount"));
        tradeResourceNameCol.setCellValueFactory(new MapValueFactory<>("resource"));
        bankResourceAmountCol.setCellValueFactory(new MapValueFactory<>("amount"));
        bankResourceNameCol.setCellValueFactory(new MapValueFactory<>("resource"));
        LOG.debug("TradeWithBankPresenter initialised");
    }

    /**
     * Handles a click on the Buy DevelopmentCard Button
     * <p>
     * If the User has the right resources, this method calls on the
     * TradeService to buy a development card
     *
     * @see de.uol.swp.common.game.request.BuyDevelopmentCardRequest
     */
    @FXML
    private void onBuyDevelopmentCardButtonPressed() {
        for (Map<String, Object> item : ownResourceTableView.getItems()) {
            if (Resources.GRAIN.equals(item.get("enumType")) && (int) item.get("amount") <= 0) return;
            if (Resources.ORE.equals(item.get("enumType")) && (int) item.get("amount") <= 0) return;
            if (Resources.WOOL.equals(item.get("enumType")) && (int) item.get("amount") <= 0) return;
        }
        tradeService.buyDevelopmentCard(lobbyName);
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
        LOG.debug("Received BuyDevelopmentCardResponse for Lobby {}", lobbyName);
        LOG.debug("---- The user got a {}", rsp.getDevelopmentCard());
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
     * as a List of resourceMaps. Calls setupHarborRatios to calculate
     * the harbor trading ratios and calls setInventories to fill the
     * inventories of the Bank and the trading selection.
     * If the user has enough resources, the buyDevelopmentButton
     * gets enabled.
     *
     * @param rsp InventoryForTradeResponse having the inventory
     *
     * @see de.uol.swp.common.game.response.InventoryForTradeResponse
     */
    @Subscribe
    private void onInventoryForTradeResponse(InventoryForTradeResponse rsp) {
        if (!lobbyName.equals(rsp.getLobbyName())) return;
        LOG.debug("Received InventoryForTradeResponse for Lobby {}", lobbyName);
        List<Map<String, Object>> resourceList = rsp.getResourceMap();
        Map<IHarborHex.HarborResource, Integer> tradingRatios = setupHarborRatios(rsp.getHarborResourceList());
        setInventories(resourceList, tradingRatios);
        boolean hasGrain = false, hasOre = false, hasWool = false;
        for (Map<String, Object> item : resourceList) {
            if (Resources.GRAIN.equals(item.get("enumType")) && (int) item.get("amount") > 0) hasGrain = true;
            if (Resources.ORE.equals(item.get("enumType")) && (int) item.get("amount") > 0) hasOre = true;
            if (Resources.WOOL.equals(item.get("enumType")) && (int) item.get("amount") > 0) hasWool = true;
        }
        buyDevelopmentButton.setDisable(!hasGrain || !hasOre || !hasWool);
    }

    /**
     * Handles a click on the Trade Button
     * <p>
     * Method called when the TradeBankButton is pressed.
     * This method checks both lists for the selected item.
     * If there is a selected item in both lists, it calls the
     * TradeService to execute the trade with the Bank.
     *
     * @see de.uol.swp.client.trade.event.TradeErrorEvent
     * @see de.uol.swp.common.game.request.ExecuteTradeWithBankRequest
     */
    @FXML
    private void onTradeResourceWithBankButtonPressed() {
        ownResourcesToTradeWith.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        if (ownResourcesToTradeWith.getSelectionModel().isEmpty()) {
            tradeService.showTradeError(resourceBundle.getString("game.error.trade.noplayerresource"));
            return;
        }
        Map<String, Object> giveResource = ownResourcesToTradeWith.getSelectionModel().getSelectedItem();
        bankResourcesView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        if (bankResourcesView.getSelectionModel().isEmpty()) {
            tradeService.showTradeError(resourceBundle.getString("game.error.trade.nobankresource"));
            return;
        }
        Map<String, Object> bankResource = bankResourcesView.getSelectionModel().getSelectedItem();
        if (bankResource == null || giveResource == null) return;
        Resources userGetsResource = (Resources) bankResource.get("enumType");
        Resources userLosesResource = (Resources) giveResource.get("enumType");
        if (userGetsResource.equals(userLosesResource)) return;
        tradeService.executeTradeWithBank(lobbyName, userGetsResource, userLosesResource);
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
        LOG.debug("Received TradeUpdateEvent for Lobby {}", lobbyName);
        Window window = ownResourcesToTradeWith.getScene().getWindow();
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
        LOG.debug("Received TradeWithBankAcceptedResponse for Lobby {}", lobbyName);
        tradeService.closeBankTradeWindow(lobbyName);
        gameService.updateInventory(lobbyName);
    }

    /**
     * Helper method to fill the three inventory TableViews
     * <p>
     * This method takes the provided player inventory and trading ratios and
     * fills the three inventories (Bank, Available Trades, Full Inventory)
     * accordingly.
     *
     * @param ownInventory  The inventory of the User trading with the Bank
     * @param tradingRatios Map of HarborResources to Integer expressing which harbors
     *                      the User possesses and therefore to which trading ratios
     *                      they are entitled
     *
     * @author Phillip-André Suhr
     * @since 2021-04-20
     */
    private void setInventories(List<Map<String, Object>> ownInventory,
                                Map<IHarborHex.HarborResource, Integer> tradingRatios) {
        for (Resources resource : Resources.values()) {
            Map<String, Object> bankResourceMap = new HashMap<>();
            bankResourceMap.put("amount", 1);
            String resourceKey = String.format("game.resources.%s", resource.name().toLowerCase());
            bankResourceMap.put("resource", new I18nWrapper(resourceKey));
            bankResourceMap.put("enumType", resource);
            bankResourcesView.getItems().add(bankResourceMap);
        }
        for (Map<String, Object> item : ownInventory) {
            Resources resource = (Resources) item.get("enumType");
            Map<String, Object> newResourceMap = new HashMap<>();
            newResourceMap.put("amount", item.get("amount"));
            newResourceMap.put("resource", item.get("resource"));
            newResourceMap.put("enumType", resource);
            ownResourceTableView.getItems().add(newResourceMap);

            IHarborHex.HarborResource harborResource = IHarborHex.HarborResource.valueOf(resource.name());
            if ((int) item.get("amount") < tradingRatios.get(harborResource)) continue;
            Map<String, Object> offerResourceMap = new HashMap<>();
            offerResourceMap.put("amount", tradingRatios.get(harborResource));
            offerResourceMap.put("resource", item.get("resource"));
            offerResourceMap.put("enumType", resource);
            ownResourcesToTradeWith.getItems().add(offerResourceMap);
        }
    }
}
