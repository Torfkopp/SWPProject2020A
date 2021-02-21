package de.uol.swp.client.trade;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.trade.event.TradeLobbyButtonUpdateEvent;
import de.uol.swp.client.trade.event.TradeUpdateEvent;
import de.uol.swp.client.trade.event.TradeWithBankCancelEvent;
import de.uol.swp.common.lobby.response.InventoryForTradeResponse;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
    @FXML
    private ListView<Pair<String, Integer>> ownRessourceView;
    @FXML
    private Button buyEntwicklungskarteButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button tradeRessourceWithBankButton;

    /**
     * Constructor
     * <p>
     * Sets the eventBus
     *
     * @param eventBus
     */
    @Inject
    public TradeWithBankPresenter(EventBus eventBus) {
        setEventBus(eventBus);
    }

    /**
     * Helper function
     * Posts a TradeWithBankCancelEvent with its lobbyName and
     * a TradeLobbyButtonUpdateEvent with the loggedInUser
     * and the lobbyName on the eventBus.
     */
    private void closeWindow() {
        eventBus.post(new TradeWithBankCancelEvent(lobbyName));
        eventBus.post(new TradeLobbyButtonUpdateEvent(loggedInUser, lobbyName));
    }

    /**
     * Initialises the Presenter by setting up the ownRessourceView.
     *
     * @implNote Called automatically by JavaFX
     */
    @FXML
    public void initialize() {
        ownRessourceView.setCellFactory(lv -> new ListCell<>() {
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
    private void onBuyEntwicklungskarteButtonPressed() {
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
        closeWindow();
    }

    /**
     * Handles a InventoryForTradeResponse found on the eventBus
     * <p>
     * If the InventoryForTradeResponse is directed to this lobby,
     * the TradeWithBankPresenter gets the inventory of the player
     * as a Map. Calls a function to fill the inventory.
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
    }

    /**
     * Handles a click on the Trade Button
     * <p>
     * Method called when the TradeBankButton is pressed.
     * The Method posts a TradeBankRequest including logged in user
     * the EventBus.
     */
    @FXML
    private void onTradeRessourceWithBankButtonPressed() {
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

        Window window = ownRessourceView.getScene().getWindow();
        window.setOnCloseRequest(windowEvent -> closeWindow());
    }

    /**
     * Helper Function
     * <p>
     * If there is no resourceList it gets created and cleared. Then it gets
     * updated with the items as listed in the ressourceMap.
     */
    private void setTradingLists() {
        if (resourceList == null) {
            resourceList = FXCollections.observableArrayList();
            ownRessourceView.setItems(resourceList);
        }
        resourceList.clear();
        for (Map.Entry<String, Integer> entry : resourceMap.entrySet()) {
            //if (entry.getValue() < 4) continue;
            Pair<String, Integer> resource = new Pair<>(entry.getKey(), entry.getValue() + 4);
            resourceList.add(resource);
        }
        System.out.println("Du hast" + resourceMap.get("ore"));
    }
}

