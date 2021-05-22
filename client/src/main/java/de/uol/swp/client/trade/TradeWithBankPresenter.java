package de.uol.swp.client.trade;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.game.IGameService;
import de.uol.swp.client.trade.event.TradeUpdateEvent;
import de.uol.swp.common.game.map.hexes.IHarborHex;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.*;
import de.uol.swp.common.game.response.BuyDevelopmentCardResponse;
import de.uol.swp.common.game.response.InventoryForTradeResponse;
import de.uol.swp.common.game.response.TradeWithBankAcceptedResponse;
import de.uol.swp.common.lobby.LobbyName;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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
@SuppressWarnings("UnstableApiUsage")
public class TradeWithBankPresenter extends AbstractTradePresenter {

    public static final String fxml = "/fxml/TradeWithBankView.fxml";
    public static final int MIN_HEIGHT = 433;
    public static final int MIN_WIDTH = 620;
    private static final Logger LOG = LogManager.getLogger(TradeWithBankPresenter.class);
    private LobbyName lobbyName;

    @Inject
    private IGameService gameService;

    @FXML
    private TableView<IResource> ownResourcesToTradeWith;
    @FXML
    private TableView<IResource> bankResourcesView;
    @FXML
    private TableColumn<IResource, Integer> tradeResourceAmountCol;
    @FXML
    private TableColumn<IResource, ResourceType> tradeResourceNameCol;
    @FXML
    private TableColumn<IResource, Integer> bankResourceAmountCol;
    @FXML
    private TableColumn<IResource, ResourceType> bankResourceNameCol;
    @FXML
    private Button buyDevelopmentButton;
    @FXML
    private Button tradeResourceWithBankButton;

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
    private static ResourceList setupHarborRatios(List<IHarborHex.HarborResource> harborMap) {
        ResourceList tradingRatio = new ResourceList();
        int prepareTradingRatio = 4;
        if (harborMap.contains(IHarborHex.HarborResource.ANY)) {
            prepareTradingRatio = 3;
        }
        for (ResourceType resourceType : ResourceType.values()) {
            tradingRatio.set(resourceType,
                             harborMap.contains(IHarborHex.getHarborResource(resourceType)) ? 2 : prepareTradingRatio);
        }
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
        tradeResourceAmountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        tradeResourceNameCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        bankResourceAmountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        bankResourceNameCol.setCellValueFactory(new PropertyValueFactory<>("type"));
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
        if (buyDevelopmentButton.isDisabled()) {
            LOG.trace("onBuyDevelopmentCardButtonPressed with disabled button, returning");
            return;
        }
        soundService.button();
        for (IResource item : ownResourceTableView.getItems()) {
            if (item.getType() == ResourceType.GRAIN && item.getAmount() <= 0) return;
            if (item.getType() == ResourceType.ORE && item.getAmount() <= 0) return;
            if (item.getType() == ResourceType.WOOL && item.getAmount() <= 0) return;
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
        soundService.button();
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
        ResourceList resourceList = rsp.getResourceList();
        ResourceList tradingRatios = setupHarborRatios(rsp.getHarborResourceList());
        setInventories(resourceList, tradingRatios);
        buyDevelopmentButton.setDisable(resourceList.getAmount(ResourceType.GRAIN) <= 0 || resourceList.getAmount(
                ResourceType.ORE) <= 0 || resourceList.getAmount(ResourceType.WOOL) <= 0);
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
        if (tradeResourceWithBankButton.isDisabled()) {
            LOG.trace("onTradeResourceWithBankButtonPressed called with disabled button, returning");
            return;
        }
        soundService.button();
        IResource bankResource;
        IResource giveResource;
        ownResourcesToTradeWith.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        if (ownResourcesToTradeWith.getSelectionModel().isEmpty()) {
            tradeService.showTradeError(resourceBundle.getString("game.error.trade.noplayerresource"));
            return;
        }
        giveResource = ownResourcesToTradeWith.getSelectionModel().getSelectedItem();
        bankResourcesView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        bankResource = bankResourcesView.getSelectionModel().getSelectedItem();
        if (bankResourcesView.getSelectionModel().isEmpty()) {
            tradeService.showTradeError(resourceBundle.getString("game.error.trade.nobankresource"));
            return;
        }
        if (bankResource != null && giveResource != null) {
            ResourceType userGetsResource = bankResource.getType();
            ResourceType userLosesResource = giveResource.getType();
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
     * <p>
     * This method also sets the accelerators for the TradeWithBankPresenter, namely
     * <ul>
     *     <li> CTRL/META + D = Buy Development Card button
     *     <li> CTRL/META + T = Trade button
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
        Map<KeyCombination, Runnable> accelerators = new HashMap<>();
        accelerators.put(new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN), // CTRL/META + D
                         this::onBuyDevelopmentCardButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.T, KeyCombination.SHORTCUT_DOWN), // CTRL/META + T
                         this::onTradeResourceWithBankButtonPressed);
        ownResourcesToTradeWith.getScene().getAccelerators().putAll(accelerators);
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
        Platform.runLater(() -> soundService.coins());
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
    private void setInventories(IResourceList ownInventory, IResourceList tradingRatios) {
        for (ResourceType resource : ResourceType.values()) {
            bankResourcesView.getItems().add(new Resource(resource, 1));
        }
        for (IResource resource : ownInventory) {
            ownResourceTableView.getItems().add(resource);
            if (resource.getAmount() >= tradingRatios.getAmount(resource.getType())) {
                ownResourcesToTradeWith.getItems().add(tradingRatios.get(resource.getType()));
            }
        }
    }
}
