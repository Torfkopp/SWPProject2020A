package de.uol.swp.client.trade;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.trade.event.TradeWithUserUpdateEvent;
import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.resourceThingies.resource.resource.MutableResource;
import de.uol.swp.common.game.resourceThingies.resource.resourceListMap.MutableResourceListMap;
import de.uol.swp.common.game.resourceThingies.resource.ResourceType;
import de.uol.swp.common.game.response.InventoryForTradeWithUserResponse;
import de.uol.swp.common.game.response.ResetOfferTradeButtonResponse;
import de.uol.swp.common.game.response.TradeOfUsersAcceptedResponse;
import de.uol.swp.common.user.UserOrDummy;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the TradingWithUser window
 *
 * @author Finn Haase
 * @author Maximilian Lindner
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-02-23
 */
@SuppressWarnings("UnstableApiUsage")
public class TradeWithUserPresenter extends AbstractTradePresenter {

    public static final String fxml = "/fxml/TradeWithUserView.fxml";
    public static final int MIN_HEIGHT = 650;
    public static final int MIN_WIDTH = 520;
    private static final Logger LOG = LogManager.getLogger(TradeWithUserPresenter.class);

    @Inject
    private ITradeService tradeService;

    @FXML
    private Label statusLabel;
    @FXML
    private HBox tradingHBox;
    @FXML
    private Slider tradingPartnerLumberSlider, tradingPartnerWoolSlider, tradingPartnerGrainSlider, tradingPartnerOreSlider, tradingPartnerBrickSlider;
    @FXML
    private Slider ownLumberSlider, ownWoolSlider, ownGrainSlider, ownOreSlider, ownBrickSlider;
    @FXML
    private ListView<MutableResource> ownInventoryView;
    @FXML
    private Button offerTradeButton;

    private LobbyName lobbyName;
    private UserOrDummy respondingUser;
    private int traderInventorySize;
    private MutableResourceListMap selectedOwnResourceMap;
    private MutableResourceListMap selectedPartnersResourceMap;
    private ObservableList<MutableResource> ownInventoryList;
    private MutableResourceListMap resourceMap;

    /**
     * Constructor
     * <p>
     * Sets the eventBus
     *
     * @param eventBus The EventBus
     */
    @Inject
    public TradeWithUserPresenter(EventBus eventBus) {
        setEventBus(eventBus);
    }

    /**
     * Initialises the Presenter using its superclass
     *
     * @implNote Called automatically by JavaFX
     */
    @FXML
    public void initialize() {
        super.initialize();
        LOG.debug("TradeWithUserPresenter initialised");
    }

    /**
     * Helper Function
     * <p>
     * Checks if there is no selected resource at all or if too
     * many resources were demanded by the offering player.
     *
     * @return true if any resource is selected
     */
    private boolean checkResources() {
        int selectedOwnResourceMapCounter = 0;
        int selectedPartnersResourceMapCounter = 0;
        for (MutableResource entry : selectedOwnResourceMap) {
            selectedOwnResourceMapCounter += entry.getAmount();
        }
        for (MutableResource entry : selectedPartnersResourceMap) {
            selectedPartnersResourceMapCounter += entry.getAmount();
        }
        if (selectedPartnersResourceMapCounter > traderInventorySize) {
            tradeService.showTradeError(resourceBundle.getString("game.trade.error.demandtoohigh"));
        }
        return ((selectedPartnersResourceMapCounter + selectedOwnResourceMapCounter == 0) || (selectedPartnersResourceMapCounter > traderInventorySize));
    }

    /**
     * Helper function called if a unsuccessful trade happened.
     * <p>
     * Posts a TradeWithBankCancelEvent with its lobbyName to close the
     * trading window and a TradeWithUserCancelResponse to close the responding
     * trading window if existent.
     *
     * @see de.uol.swp.client.trade.event.TradeCancelEvent
     */
    private void closeWindow() {
        tradeService.closeUserTradeWindow(lobbyName);
        tradeService.cancelTrade(lobbyName, respondingUser);
    }

    /**
     * Handles a click on the Cancel Button
     * <p>
     * Method called when the CancelButton is pressed and uses
     * the helperFunction closeWindow to close the window
     * properly.
     */
    @FXML
    private void onCancelTradeButtonPressed() {
        closeWindow();
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
     *
     * @see de.uol.swp.common.game.response.InventoryForTradeWithUserResponse
     */
    @Subscribe
    private void onInventoryForTradeWithUserResponse(InventoryForTradeWithUserResponse rsp) {
        if (!rsp.getLobbyName().equals(this.lobbyName)) return;
        LOG.debug("Received InventoryForTradeResponse for Lobby " + rsp.getLobbyName());
        respondingUser = rsp.getTradingUser();
        resourceMap = rsp.getResourceMap();
        setTradingLists();
        traderInventorySize = rsp.getTradingUsersInventorySize();
        int ownInventorySize = 0;
        for (MutableResource entry : resourceMap) {
            ownInventorySize += entry.getAmount();
        }
        if (!(traderInventorySize == 0 && ownInventorySize == 0)) {
            setSliders();
            Platform.runLater(() -> statusLabel
                    .setText(String.format(resourceBundle.getString("game.trade.status.makingoffer"), respondingUser)));
        } else {
            offerTradeButton.setDisable(true);
            tradingHBox.setVisible(false);
            Platform.runLater(() -> statusLabel
                    .setText(String.format(resourceBundle.getString("game.trade.error.noresources"), respondingUser)));
        }
    }

    /**
     * Handles a Click on the OfferTrade button
     * <p>
     * If the button is clicked, this method calls the setResourceMap method.
     * If there is no resource selected at all, nothing happens.
     * Otherwise, an OfferingTradeWithUserRequest is posted
     * onto the EventBus to get the needed information from
     * the server.
     * The offerTradeButton gets disabled and the user gets the message to wait
     * for the other user.
     *
     * @see de.uol.swp.common.game.request.OfferingTradeWithUserRequest
     */
    @FXML
    private void onOfferTradeButtonPressed() {
        setResourceMaps();
        if (checkResources()) {
            LOG.debug("Failed sending the offer");
            return;
        }
        offerTradeButton.setDisable(true);
        statusLabel.setText(String.format(resourceBundle.getString("game.trade.status.waiting"), respondingUser));
        tradeService.offerTrade(lobbyName, respondingUser, selectedOwnResourceMap, selectedPartnersResourceMap);
        tradeService.closeTradeResponseWindow(lobbyName);
    }

    /**
     * Handles a ResetOfferTradeButtonResponse found on the EventBus
     * <p>
     * If a ResetOfferTradeButtonResponse is found on the EventBus, the offer trade button
     * is re-enabled and the trading user gets a hint that the other user
     * rejected the offer.
     *
     * @param event ResetOfferTradeButtonResponse found on the EventBus
     *
     * @see de.uol.swp.common.game.response.ResetOfferTradeButtonResponse
     */
    @Subscribe
    private void onResetOfferTradeButtonResponse(ResetOfferTradeButtonResponse event) {
        if (!lobbyName.equals(event.getLobbyName())) return;
        LOG.debug("Received ResetOfferTradeButtonResponse for Lobby " + this.lobbyName);
        Platform.runLater(() -> {
            offerTradeButton.setDisable(false);
            statusLabel.setText(String.format(resourceBundle.getString("game.trade.status.rejected"), respondingUser));
        });
    }

    /**
     * Handles a TradeOfUsersAcceptedResponse found on the EventBus
     * If the trade is accepted this will close the trading window.
     *
     * @param rsp Response found on the EventBus
     *
     * @see de.uol.swp.common.game.response.TradeOfUsersAcceptedResponse
     */
    @Subscribe
    private void onTradeOfUsersAcceptedResponse(TradeOfUsersAcceptedResponse rsp) {
        if (!rsp.getLobbyName().equals(this.lobbyName)) return;
        LOG.debug("Received TradeOfUsersAcceptedResponse for Lobby " + this.lobbyName);
        closeWindow();
    }

    /**
     * Handles a TradeWithUserUpdateEvent
     * <p>
     * If the lobbyName or the logged in user of the TradeWithUserPresenter are
     * null, they get the parameters of the event. This Event is sent when a new
     * TradeWithUserPresenter is created. If a window is closed using e.g.
     * X(top-right-Button), the closeWindow method is called.
     *
     * @param event TradeUpdateEvent found on the event bus
     *
     * @see de.uol.swp.client.trade.event.TradeWithUserUpdateEvent
     */
    @Subscribe
    private void onTradeWithUserUpdateEvent(TradeWithUserUpdateEvent event) {
        LOG.debug("Received TradeWithUserUpdateEvent for Lobby " + event.getLobbyName());
        if (lobbyName == null) lobbyName = event.getLobbyName();
        Window window = ownInventoryView.getScene().getWindow();
        window.setOnCloseRequest(windowEvent -> closeWindow());
    }

    /**
     * Helper function
     * <p>
     * Sets the content of resource maps according to the selected resources
     * and the amount of resources as selected with the sliders.
     */
    @FXML
    private void setResourceMaps() {
        selectedOwnResourceMap = new MutableResourceListMap();
        selectedOwnResourceMap.set(ResourceType.BRICK, ((int) (ownBrickSlider.getValue())));
        selectedOwnResourceMap.set(ResourceType.ORE, ((int) (ownOreSlider.getValue())));
        selectedOwnResourceMap.set(ResourceType.LUMBER, ((int) (ownLumberSlider.getValue())));
        selectedOwnResourceMap.set(ResourceType.GRAIN, ((int) (ownGrainSlider.getValue())));
        selectedOwnResourceMap.set(ResourceType.WOOL, ((int) (ownWoolSlider.getValue())));

        selectedPartnersResourceMap = new MutableResourceListMap();
        selectedPartnersResourceMap.set(ResourceType.BRICK, ((int) (tradingPartnerBrickSlider.getValue())));
        selectedPartnersResourceMap.set(ResourceType.ORE, ((int) (tradingPartnerOreSlider.getValue())));
        selectedPartnersResourceMap.set(ResourceType.WOOL, ((int) (tradingPartnerWoolSlider.getValue())));
        selectedPartnersResourceMap.set(ResourceType.LUMBER, ((int) (tradingPartnerLumberSlider.getValue())));
        selectedPartnersResourceMap.set(ResourceType.GRAIN, ((int) (tradingPartnerGrainSlider.getValue())));
    }

    /**
     * Helper Function to handle the slider attributes
     */
    @FXML
    private void setSliders() {
        tradingPartnerBrickSlider.setMax(traderInventorySize);
        tradingPartnerOreSlider.setMax(traderInventorySize);
        tradingPartnerLumberSlider.setMax(traderInventorySize);
        tradingPartnerWoolSlider.setMax(traderInventorySize);
        tradingPartnerGrainSlider.setMax(traderInventorySize);

        ownGrainSlider.setMax(resourceMap.getAmount(ResourceType.GRAIN));
        ownOreSlider.setMax(resourceMap.getAmount(ResourceType.ORE));
        ownLumberSlider.setMax(resourceMap.getAmount(ResourceType.LUMBER));
        ownWoolSlider.setMax(resourceMap.getAmount(ResourceType.WOOL));
        ownBrickSlider.setMax(resourceMap.getAmount(ResourceType.BRICK));
    }

    /**
     * Helper Function
     * <p>
     * If there is no resourceList one gets created and cleared. Then it gets
     * updated with the items as listed in the resourceMap.
     */
    private void setTradingLists() {
        if (ownInventoryList == null) {
            ownInventoryList = FXCollections.observableArrayList();
            ownInventoryView.setItems(ownInventoryList);
        }
        ownInventoryList.clear();
        for (MutableResource entry : resourceMap) {
            ownInventoryList.add(entry.create());
        }
    }
}
