package de.uol.swp.client.trade;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.trade.event.CloseTradeWithUserResponseEvent;
import de.uol.swp.client.trade.event.ShowTradeWithUserViewEvent;
import de.uol.swp.client.trade.event.TradeWithUserResponseUpdateEvent;
import de.uol.swp.common.game.request.AcceptUserTradeRequest;
import de.uol.swp.common.game.request.ResetOfferTradeButtonRequest;
import de.uol.swp.common.game.request.TradeWithUserRequest;
import de.uol.swp.common.game.response.InvalidTradeOfUsersResponse;
import de.uol.swp.common.game.response.TradeOfUsersAcceptedResponse;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Manages the trading accept menu
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021 -02-25
 */
@SuppressWarnings("UnstableApiUsage")
public class TradeWithUserAcceptPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/TradeWithUserAcceptView.fxml";
    private static final Logger LOG = LogManager.getLogger(TradeWithUserAcceptPresenter.class);
    @FXML
    private Button acceptTradeButton;
    @FXML
    private Label tradeNotPossibleLabel;
    @FXML
    private Label tradeResponseLabel;
    @FXML
    private ListView<Pair<String, Integer>> ownInventoryView;

    private String lobbyName;
    private String offeringUserName;
    private User respondingUser;
    private Map<String, Integer> offeringResourceMap;
    private Map<String, Integer> resourceMap;
    private Map<String, Integer> respondingResourceMap;
    private ObservableList<Pair<String, Integer>> ownInventoryList;

    /**
     * Constructor
     *
     * @param eventBus The EventBus
     */
    @Inject
    public TradeWithUserAcceptPresenter(EventBus eventBus) {
        setEventBus(eventBus);
    }

    /**
     * Initialises the Presenter by setting up the ownInventoryView.
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
        LOG.debug("TradeWithUserAcceptPresenter initialised");
    }

    /**
     * Handles a click on the MakeCounterOfferButton
     * <p>
     * When the buttons gets pressed, this method calls the
     * ShowTradeWithUserViewEvent to open up the trading window and
     * a TradeWithUserRequest to get the needed information from the
     * server for the trade.
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @see de.uol.swp.client.trade.event.ShowTradeWithUserViewEvent
     * @see de.uol.swp.common.game.request.TradeWithUserRequest
     * @since 2021-03-19
     */
    @FXML
    private void onMakeCounterOfferButtonPressed() {
        LOG.debug("Sending ShowTradeWithUserViewEvent");
        eventBus.post(new ShowTradeWithUserViewEvent(respondingUser, this.lobbyName, offeringUserName));
        LOG.debug("Sending a TradeWithUserRequest for Lobby " + this.lobbyName);
        eventBus.post(new TradeWithUserRequest(this.lobbyName, respondingUser, offeringUserName));
    }

    /**
     * Helper function
     * <p>
     * This method is called if a window should be closed.
     * It posts a CloseTradeWithUserResponseEvent onto
     * the EventBus with the according lobby.
     */
    private void closeWindow() {
        Platform.runLater(() -> eventBus.post(new CloseTradeWithUserResponseEvent(lobbyName)));
    }

    /**
     * Handles a click on the accept button
     * If the accept button is pressed a new AcceptUserTradeRequest is posted onto
     * the EventBus.
     */
    @FXML
    private void onAcceptTradeButtonPressed() {
        eventBus.post(new AcceptUserTradeRequest(respondingUser.getUsername(), offeringUserName, lobbyName,
                                                 respondingResourceMap, offeringResourceMap));
    }

    /**
     * Handles a InvalidTradeOfUsersResponse found on the EventBus
     * <p>
     * If an InvalidTradeOfUsersResponse is found on the EventBus, this
     * method disables the accept button and shows an info text.
     *
     * @param rsp The InvalidTradeOfUsersResponse found on the EventBus
     */
    @Subscribe
    private void onInvalidTradeOfUsersResponse(InvalidTradeOfUsersResponse rsp) {
        Platform.runLater(() -> {
            LOG.debug("Received InvalidTradeOfUsersResponse for Lobby " + this.lobbyName);
            acceptTradeButton.setDisable(true);
            tradeNotPossibleLabel.setText(String.format(resourceBundle.getString("game.trade.status.invalid"),
                                                        rsp.getOfferingUserName().getUsername()));
        });
    }

    /**
     * Handles a click on the reject button
     * <p>
     * If the lobbyname or the logged in user of the TradeWithUserPresenter are
     * null, they get the parameters of the event. This Event is sent when a new
     * TradeWithUserPresenter is created. If a window is closed using e.g.
     * X(top-right-Button), the closeWindowAfterNotSuccessfulTrade method is called.
     */
    @FXML
    private void onRejectTradeButtonPressed() {
        eventBus.post(new ResetOfferTradeButtonRequest(lobbyName, offeringUserName));
        closeWindow();
    }

    /**
     * This method calls the closeWindow function to close the
     * according window.
     *
     * @param rsp TradeOfUsersAcceptedResponse found on the EventBus
     *
     * @see de.uol.swp.common.game.response.TradeOfUsersAcceptedResponse
     */
    @Subscribe
    private void onTradeOfUsersAcceptedResponse(TradeOfUsersAcceptedResponse rsp) {
        LOG.debug("Received TradeOfUsersAcceptedResponse for Lobby " + this.lobbyName);
        closeWindow();
    }

    /**
     * Handles a TradeWithUserResponseUpdateEvent found on the EventBus
     * <p>
     * If a TradeWithUserResponseUpdateEvent is found on the EventBus
     * and it is directed to this lobby, this TradeWithUserAcceptPresenter
     * gets multiple Parameters and calls the setOfferLabel method to
     * set the offer label according to the offer and the setTradingList
     * to set the inventory according to the responding user´s inventory.
     *
     * @param event TradeWithUserResponseUpdateEvent found on the EventBus
     */
    @Subscribe
    private void onTradeWithUserResponseUpdateEvent(TradeWithUserResponseUpdateEvent event) {
        TradeWithUserOfferResponse rsp = event.getRsp();
        lobbyName = rsp.getLobbyName();
        if (!lobbyName.equals(rsp.getLobbyName())) return;
        LOG.debug("Received TradeWithUserResponseUpdateEvent for Lobby " + this.lobbyName);
        respondingUser = rsp.getRespondingUser();
        offeringUserName = rsp.getOfferingUser().getUsername();
        respondingResourceMap = rsp.getRespondingResourceMap();
        offeringResourceMap = rsp.getOfferingResourceMap();
        resourceMap = rsp.getResourceMap();
        setTradingList();
        setOfferLabel();
        Window window = ownInventoryView.getScene().getWindow();
        window.setOnCloseRequest(windowEvent -> closeWindow());
    }

    /**
     * Helper Function
     * <p>
     * Sets the content of the tradeResponseLabel to the offers and demands
     */
    private void setOfferLabel() {
        LOG.debug("Setting the tradeResponseLabel");
        boolean offer = false;
        StringBuilder offerText = new StringBuilder(
                String.format(resourceBundle.getString("game.trade.offer.proposed"), offeringUserName)).append("\n");

        for (Map.Entry<String, Integer> entry : offeringResourceMap.entrySet()) {
            int amount = entry.getValue();
            if (amount > 0) {
                offer = true;
                offerText.append(entry.getValue()).append(" ")
                         .append(resourceBundle.getString("game.resources." + entry.getKey())).append(", ");
            }
        }
        if (offerText.substring(offerText.length() - 2, offerText.length()).equals(", "))
            offerText.delete(offerText.length() - 2, offerText.length());
        if (!offer) offerText.append(resourceBundle.getString("game.trade.offer.nothing"));
        boolean demand = false;
        offerText.append("\n").append(resourceBundle.getString("game.trade.offer.demand")).append("\n");

        for (Map.Entry<String, Integer> entry : respondingResourceMap.entrySet()) {
            int amount = entry.getValue();
            if (amount > 0) {
                demand = true;
                offerText.append(entry.getValue()).append(" ")
                         .append(resourceBundle.getString("game.resources." + entry.getKey())).append(", ");
            }
        }
        if (offerText.substring(offerText.length() - 2, offerText.length()).equals(", "))
            offerText.delete(offerText.length() - 2, offerText.length());
        if (!demand) offerText.append(resourceBundle.getString("game.trade.offer.nothing"));
        Platform.runLater(() -> tradeResponseLabel.setText(offerText.append(".").toString()));
    }

    /**
     * Helper Function
     * <p>
     * Sets the content of the InventoryView
     */
    private void setTradingList() {
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
