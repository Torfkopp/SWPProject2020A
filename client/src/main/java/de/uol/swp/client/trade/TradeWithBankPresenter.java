package de.uol.swp.client.trade;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.lobby.event.LobbyErrorEvent;
import de.uol.swp.client.trade.event.*;
import de.uol.swp.common.game.request.BuyDevelopmentCardRequest;
import de.uol.swp.common.game.request.UpdateInventoryAfterTradeWithBankRequest;
import de.uol.swp.common.game.request.UpdateInventoryRequest;
import de.uol.swp.common.game.response.BuyDevelopmentCardResponse;
import de.uol.swp.common.game.response.InventoryForTradeResponse;
import de.uol.swp.common.game.response.TradeWithBankAcceptedResponse;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Manages the Trading with the bank window
 *
 * @author Alwin Bossert
 * @author Maximilian Lindner
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-02-19
 */
@SuppressWarnings("UnstableApiUsage")
public class TradeWithBankPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/TradeWithBankView.fxml";
    private final Logger LOG = LogManager.getLogger(TradeWithBankPresenter.class);
    private String lobbyName;
    private User loggedInUser;
    private Map<String, Integer> resourceMap;
    private ObservableList<Pair<String, Integer>> resourceList;
    private ObservableList<Pair<String, Integer>> bankResourceList;
    private ObservableList<Pair<String, Integer>> ownInventoryList;
    @FXML
    private ListView<Pair<String, Integer>> ownInventoryView;
    @FXML
    private ListView<Pair<String, Integer>> ownResourceToTradeWithView;
    @FXML
    private ListView<Pair<String, Integer>> bankResourceView;
    @FXML
    private Button buyDevelopmentButton;
    @FXML
    private Button cancelButton;
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
     * Helper function called if a not successfull trade happened.
     * Posts a TradeWithBankCancelEvent with its lobbyName to close the
     * trading window and a TradeLobbyButtonUpdateEvent with the
     * loggedInUser and the lobbyName on the eventBus to update the
     * button statuses in the lobby..
     */
    private void closeWindowAfterNotSuccessfulTrade() {
        Platform.runLater(() -> {
            eventBus.post(new TradeWithBankCancelEvent(lobbyName));
            eventBus.post(new ResetTradeWithBankButtonEvent(loggedInUser, lobbyName));
        });
    }

    /**
     * Helper function called if a successfull trade happened.
     * Posts a TradeWithBankCancelEvent with its lobbyName to close the
     * trading window and TradeLobbyButtonUpdateEvent with the
     * loggedInUser and the lobbyName on the eventBus to update the
     * button statuses in the lobby.
     */
    private void closeWindowAfterSuccessfulTrade() {
        Platform.runLater(() -> {
            eventBus.post(new TradeWithBankCancelEvent(lobbyName));
            eventBus.post(new TradeLobbyButtonUpdateEvent(loggedInUser, lobbyName));
        });
    }

    /**
     * Initialises the Presenter by setting up the ownResourceView.
     *
     * @implNote Called automatically by JavaFX
     */
    @FXML
    public void initialize() {
        ownResourceToTradeWithView.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Pair<String, Integer> item, boolean empty) {
                Platform.runLater(() -> {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getValue().toString() + " " + resourceBundle
                            .getString("game.resources." + item.getKey())); // looks like: "1 Brick"
                });
            }
        });
        bankResourceView.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Pair<String, Integer> item, boolean empty) {
                Platform.runLater(() -> {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getValue().toString() + " " + resourceBundle
                            .getString("game.resources." + item.getKey()));
                });
            }
        });
        ownInventoryView.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Pair<String, Integer> item, boolean empty) {
                Platform.runLater(() -> {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getValue().toString() + " " + resourceBundle
                            .getString("game.resources." + item.getKey())); // looks like: "1 Brick"
                });
            }
        });
        LOG.debug("TradeWithBankPresenter initialised");
    }

    /**
     * Handles a click on the Buy Button
     * <p>
     * Method called when the BuyBankButton is pressed.
     * The Method posts a BuyBankRequest including logged in user
     * the EventBus.
     */
    @FXML
    public void onBuyDevelopmentCardButtonPressed(ActionEvent actionEvent) {
        if (resourceMap.get("ore") >= 1 && resourceMap.get("grain") >= 1 && resourceMap.get("wool") >= 1) {
            Message buyDevelopmentCardRequest = new BuyDevelopmentCardRequest(loggedInUser, lobbyName);
            eventBus.post(buyDevelopmentCardRequest);
        }
    }

    /**
     * If a BuyDevelopmentCardResponse is found on the event bus,
     * this method calls the close method, which closes the trading
     * window and posts a updateInventoryRequest onto the event bus
     * to get the new Inventory after the trade shown in the
     * LobbyView.
     *
     * @param response The BuyDevelopmentCardResponse found on the eventBus
     *
     * @implNote the User has to check what card he got by looking at his inventory or check the log
     */
    @Subscribe
    private void onBuyDevelopmentCardResponse(BuyDevelopmentCardResponse response) {
        LOG.debug("Received BuyDevelopmentCardResponse for Lobby " + this.lobbyName);
        if (lobbyName.equals(response.getLobbyName())) {
            closeWindowAfterSuccessfulTrade();
            LOG.debug("The user got a " + response.getDevelopmentCard());
            LOG.debug("Sending UpdateInventoryRequest");
            Message updateInventoryRequest = new UpdateInventoryRequest(loggedInUser, lobbyName);
            eventBus.post(updateInventoryRequest);
            tradeResourceWithBankButton.setDisable(true);
        }
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
        closeWindowAfterNotSuccessfulTrade();
    }

    /**
     * Handles a InventoryForTradeResponse found on the eventBus
     * <p>
     * If the InventoryForTradeResponse is directed to this lobby,
     * the TradeWithBankPresenter gets the inventory of the player
     * as a Map. Calls a function to fill the inventory.
     * If the user has enough resources, the buy
     *
     * @param response InventoryForTradeResponse having the inventory
     */
    @Subscribe
    private void onInventoryForTradeResponse(InventoryForTradeResponse response) {
        if (response.getLobbyName().equals(this.lobbyName)) {
            LOG.debug("Received InventorForTradeResponse for Lobby " + this.lobbyName);
            resourceMap = response.getResourceMap();
            setTradingLists();
        }
        if (resourceMap.get("ore") >= 1 && resourceMap.get("grain") >= 1 && resourceMap.get("wool") >= 1) {
            buyDevelopmentButton.setDisable(false);
        }
    }

    /**
     * Handles a click on the Trade Button
     * <p>
     * Method called when the TradeBankButton is pressed.
     * This method checks both lists for the selected item.
     * If there is a selected item in both lists, it posts a UpdateInventoryAfterTradeWithBankRequest
     * onto the EventBus.
     */
    @FXML
    private void onTradeResourceWithBankButtonPressed() {
        Pair<String, Integer> bankResource;
        Pair<String, Integer> giveResource;
        ownResourceToTradeWithView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        if (ownResourceToTradeWithView.getSelectionModel().isEmpty()) {
            eventBus.post(new LobbyErrorEvent("You have to choose one of your resources in the top left list"));
            return;
        } else {
            giveResource = ownResourceToTradeWithView.getSelectionModel().getSelectedItem();
        }
        bankResourceView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        if (bankResourceView.getSelectionModel().isEmpty()) {
            eventBus.post(new LobbyErrorEvent("You have to choose one of the resources the bank offers you"));
            return;
        } else {
            bankResource = bankResourceView.getSelectionModel().getSelectedItem();
        }
        if (bankResource != null && giveResource != null) {
            String userGetsResource = bankResource.getKey();
            String userLosesResource = giveResource.getKey();
            if (userGetsResource.equals(userLosesResource)) return;
            LOG.debug("Sending a UpdateInventoryAfterTradeWithBankRequest for Lobby " + this.lobbyName);
            Message updateInventoryAfterTradeWithBankRequest = new UpdateInventoryAfterTradeWithBankRequest(
                    loggedInUser, lobbyName, userGetsResource, userLosesResource);
            eventBus.post(updateInventoryAfterTradeWithBankRequest);
        }
    }

    /**
     * Handles a TradeUpdateEvent
     * <p>
     * If the lobbyname and the logged in user of the TradeWithBankPresenter are
     * null, they get the parameters of the event. This Event is sent when a new
     * TradeWithBankPresenter is created. If a window is closed using the
     * X(top-right-Button), the closeWindow method is called.
     *
     * @param event TradeUpdateEvent found on the event bus
     */
    @Subscribe
    private void onTradeUpdateEvent(TradeUpdateEvent event) {
        if (lobbyName == null && loggedInUser == null) {
            lobbyName = event.getLobbyName();
            loggedInUser = event.getUser();
        }
        LOG.debug("Received TradeUpdateEvent for Lobby " + this.lobbyName);

        Window window = ownResourceToTradeWithView.getScene().getWindow();
        window.setOnCloseRequest(windowEvent -> closeWindowAfterNotSuccessfulTrade());
    }

    /**
     * If a TradeWithBankAcceptedResponse is found on the event bus,
     * this method calls the close method, which closes the trading
     * window and posts a updateInventoryRequest onto the event bus
     * to get the new Inventory after the trade shown in the
     * LobbyView.
     *
     * @param response TradeWithBankButtonAcceptedResponse found on the event bus
     */
    @Subscribe
    private void onTradeWithBankAcceptedResponse(TradeWithBankAcceptedResponse response) {
        LOG.debug("Received TradeWithBankAcceptedResponse for Lobby " + this.lobbyName);
        if (lobbyName.equals(response.getLobbyName())) {
            closeWindowAfterSuccessfulTrade();
            LOG.debug("Sending UpdateInventoryRequest");
            Message updateInventoryRequest = new UpdateInventoryRequest(loggedInUser, lobbyName);
            eventBus.post(updateInventoryRequest);
            tradeResourceWithBankButton.setVisible(false);
            tradeResourceWithBankButton.setDisable(true);
        }
    }

    /**
     * Helper Function
     * <p>
     * If there is no resourceList it gets created and cleared. Then it gets
     * updated with the items as listed in the resourceMap.
     */
    private void setTradingLists() {
        if (resourceList == null) {
            resourceList = FXCollections.observableArrayList();
            ownInventoryList = FXCollections.observableArrayList();
            ownResourceToTradeWithView.setItems(resourceList);
            ownInventoryView.setItems(ownInventoryList);
        }
        resourceList.clear();
        ownInventoryList.clear();
        for (Map.Entry<String, Integer> entry : resourceMap.entrySet()) {
            Pair<String, Integer> ownResource = new Pair<>(entry.getKey(), entry.getValue());
            ownInventoryList.add(ownResource);
            if (entry.getValue() < 4) continue;
            Pair<String, Integer> resource = new Pair<>(entry.getKey(), 4);
            resourceList.add(resource);
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
            Pair<String, Integer> resource = new Pair<>(entry.getKey(), 1);
            bankResourceList.add(resource);
        }
    }
}
