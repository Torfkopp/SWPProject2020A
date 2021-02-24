package de.uol.swp.client.trade;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.trade.event.ResetTradeWithUserButtonEvent;
import de.uol.swp.client.trade.event.TradeWithUserCancelEvent;
import de.uol.swp.client.trade.event.TradeWithUserUpdateEvent;
import de.uol.swp.common.game.response.InventoryForTradeWithUserResponse;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Manages the Trading with the user window
 *
 * @author Finn Haase
 * @author Maximilian Lindner
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-02-23
 */
@SuppressWarnings("UnstableApiUsage")
public class TradeWithUserPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/TradeWithUserView.fxml";
    private final Logger LOG = LogManager.getLogger(TradeWithUserPresenter.class);
    @FXML
    private Label noResourcesLabel;
    @FXML
    private HBox tradingHBox;
    @FXML
    private Slider tradingPartnerLumberSlider, tradingPartnerWoolSlider, tradingPartnerGrainSlider, tradingPartnerOreSlider, tradingPartnerBrickSlider;
    @FXML
    private Slider ownLumberSlider, ownWoolSlider, ownGrainSlider, ownOreSlider, ownBrickSlider;
    private String lobbyName;
    private User loggedInUser;
    private int traderInventorySize;
    private int ownInventorySize;
    private ObservableList<Pair<String, Integer>> ownInventoryList;
    private Map<String, Integer> resourceMap;
    @FXML
    private ListView<Pair<String, Integer>> ownInventoryView;
    @FXML
    private Button cancelTradeButton;
    @FXML
    private Button offerTradeButton;

    /**
     * Constructor
     * <p>
     * Sets the eventBus
     *
     * @param eventBus EventBus
     */
    @Inject
    public TradeWithUserPresenter(EventBus eventBus) {
        setEventBus(eventBus);
    }

    /**
     * Helper function called if a unsuccessful trade happened.
     * <p>
     * Posts a TradeWithBankCancelEvent with its lobbyName to close the
     * trading window.
     */
    private void closeWindowAfterNotSuccessfulTrade() {
        Platform.runLater(() -> {
            eventBus.post(new TradeWithUserCancelEvent(lobbyName));
            eventBus.post(new ResetTradeWithUserButtonEvent(loggedInUser, lobbyName));
        });
    }

    /**
     * Initialises the Presenter by setting up the ownResourceView.
     *
     * @implNote Called automatically by JavaFX
     */
    @FXML
    public void initialize() {

        ownInventoryView.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Pair<String, Integer> item, boolean empty) {
                Platform.runLater(() -> {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getValue().toString() + " " + resourceBundle
                            .getString("game.resources." + item.getKey()));
                });
            }
        });
        LOG.debug("TradeWithUserPresenter initialised");
    }

    /**
     * Handles a click on the Cancel Button
     * <p>
     * Method called when the CancelButton is pressed and uses
     * the helperFunction closeWindowAfterNotSuccessfulTrade to close the window
     * properly.
     */
    @FXML
    private void onCancelTradeButtonPressed() {
        closeWindowAfterNotSuccessfulTrade();
    }

    /**
     * Handles a InventoryForTradeWithUserResponse found on the eventBus
     * <p>
     * If the InventoryForTradeWithUserResponse is directed to this lobby,
     * the TradeWithBankPresenter gets the inventory of the player
     * as a Map. Calls a function to fill the inventory and calls a function
     * to give the sliders the right labeling.
     * If the user has no resources and the amount of resources
     * of the other user is also 0, the trading possibility gets denied by
     * hiding the inventories and the button and showing the label instead.
     *
     * @param rsp InventoryForTradeResponse having the inventory
     */
    @Subscribe
    private void onInventoryForTradeResponse(InventoryForTradeWithUserResponse rsp) {
        if (rsp.getLobbyName().equals(this.lobbyName)) {
            LOG.debug("Received InventoryForTradeResponse for Lobby " + rsp.getLobbyName());
            resourceMap = rsp.getResourceMap();
            setTradingLists();
            traderInventorySize = rsp.getTradingUsersInventorySize();
            ownInventorySize = 0;
            ownInventorySize += resourceMap.get("wool");
            ownInventorySize += resourceMap.get("brick");
            ownInventorySize += resourceMap.get("ore");
            ownInventorySize += resourceMap.get("lumber");
            if (!(traderInventorySize == 0 && ownInventorySize == 0)) {
                setSliders();
            } else {
                ownInventorySize += resourceMap.get("grain");
                offerTradeButton.setDisable(true);
                tradingHBox.setVisible(false);
                noResourcesLabel.setVisible(true);
            }
        }
    }

    /**
     * Handles a Click on the OfferTrade button
     */
    @FXML
    private void onOfferTradeButtonPressed() {

    }

    /**
     * Handles a TradeWithUserUpdateEvent
     * <p>
     * If the lobbyname or the logged in user of the TradeWithUserPresenter are
     * null, they get the parameters of the event. This Event is sent when a new
     * TradeWithUserPresenter is created. If a window is closed using e.g.
     * X(top-right-Button), the closeWindowAfterNotSuccessfulTrade method is called.
     *
     * @param event TradeUpdateEvent found on the event bus
     */
    @Subscribe
    private void onTradeWithUserUpdateEvent(TradeWithUserUpdateEvent event) {
        LOG.debug("Received TradeWithUserUpdateEvent for Lobby " + event.getLobbyName());
        if (lobbyName == null || loggedInUser == null) {
            lobbyName = event.getLobbyName();
            loggedInUser = event.getUser();
        }
        Window window = ownInventoryView.getScene().getWindow();
        window.setOnCloseRequest(windowEvent -> closeWindowAfterNotSuccessfulTrade());
    }

    /**
     * Helper Function to handle the slider attributes
     */
    private void setSliders() {
        System.out.println("Sliders werden gesetzt");
        tradingPartnerBrickSlider.setMax(traderInventorySize);
        tradingPartnerOreSlider.setMax(traderInventorySize);
        tradingPartnerLumberSlider.setMax(traderInventorySize);
        tradingPartnerWoolSlider.setMax(traderInventorySize);
        tradingPartnerGrainSlider.setMax(traderInventorySize);
        ownGrainSlider.setMax(resourceMap.get("grain"));
        ownOreSlider.setMax(resourceMap.get("ore"));
        ownLumberSlider.setMax(resourceMap.get("lumber"));
        ownWoolSlider.setMax(resourceMap.get("wool"));
        ownBrickSlider.setMax(resourceMap.get("brick"));
    }

    /**
     * Helper Function
     * <p>
     * If there is no resourceList it gets created and cleared. Then it gets
     * updated with the items as listed in the resourceMap.
     */
    private void setTradingLists() {
        if (ownInventoryList == null) {
            ownInventoryList = FXCollections.observableArrayList();
            ownInventoryView.setItems(ownInventoryList);
        }
        ownInventoryList.clear();
        for (Map.Entry<String, Integer> entry : resourceMap.entrySet()) {
            Pair<String, Integer> ownResource = new Pair<>(entry.getKey(), entry.getValue());
            ownInventoryList.add(ownResource);
        }
    }
}
