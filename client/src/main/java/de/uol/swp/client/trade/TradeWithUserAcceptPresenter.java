package de.uol.swp.client.trade;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.trade.event.CloseTradeWithUserResponseEvent;
import de.uol.swp.client.trade.event.ResetOfferTradeButtonEvent;
import de.uol.swp.client.trade.event.TradeWithUserResponseUpdateEvent;
import de.uol.swp.common.game.message.TradeWithUserOfferMessage;
import de.uol.swp.common.game.request.AcceptUserTradeRequest;
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

@SuppressWarnings("ALL")
public class TradeWithUserAcceptPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/TradeWithUserAcceptView.fxml";
    private final Logger LOG = LogManager.getLogger(TradeWithUserAcceptPresenter.class);
    private Map<String, Integer> resourceMap;
    @FXML
    private Label tradeResponseLabel;
    private User offeringUser;
    private String respondingUser;
    private String lobbyName;
    private Map<String, Integer> offeringResourceMap;
    private Map<String, Integer> respondingResourceMap;
    private ObservableList<Pair<String, Integer>> ownInventoryList;
    @FXML
    private Button rejectTradeButton;
    @FXML
    private Button acceptTradeButton;
    @FXML
    private ListView<Pair<String, Integer>> ownInventoryView;

    @Inject
    public TradeWithUserAcceptPresenter(EventBus eventBus) {
        setEventBus(eventBus);
    }

    private void closeWindow() {
        Platform.runLater(() -> {
            eventBus.post(new CloseTradeWithUserResponseEvent(lobbyName));
            eventBus.post(new ResetOfferTradeButtonEvent(lobbyName));
        });
    }

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

    @FXML
    private void onAcceptTradeButtonPressed() {
        eventBus.post(
                new AcceptUserTradeRequest(respondingUser, offeringUser.getUsername(), lobbyName, respondingResourceMap,
                                           offeringResourceMap));
        //todo Tausch
    }

    /**
     * Handles a click on the reject button
     * <p>
     * If the lobbyname or the logged in user of the TradeWithUserPresenter are
     * null, they get the parameters of the event. This Event is sent when a new
     * TradeWithUserPresenter is created. If a window is closed using e.g.
     * X(top-right-Button), the closeWindowAfterNotSuccessfulTrade method is called.
     *
     * @param event TradeUpdateEvent found on the event bus
     */
    @FXML
    private void onRejectTradeButtonPressed() {
        closeWindow();
    }

    @Subscribe
    private void onTradeWithUserOfferMessage(TradeWithUserOfferMessage message) {
        if (lobbyName.equals(message.getLobbyName()) && respondingUser.equals(message.getRespondingUserName())) {
            respondingResourceMap = message.getRespondingResourceMap();
            offeringResourceMap = message.getOfferingResourceMap();
            resourceMap = message.getResourceMap();
            System.out.println(resourceMap.size());
            setTradingList();
            setLabel();
        }
        Window window = ownInventoryView.getScene().getWindow();
        window.setOnCloseRequest(windowEvent -> closeWindow());
    }

    @Subscribe
    private void onTradeWithUserResponseUpdateEvent(TradeWithUserResponseUpdateEvent event) {
        //todo Fenster nur bei responding user anzeigen
        offeringUser = event.getOfferingUser();
        respondingUser = event.getResponseUser();
        lobbyName = event.getLobbyName();
    }

    /**
     * Helper Function
     * <p>
     * Gives the Label the right content
     */
    private void setLabel() {
        LOG.debug("Setting the Label");
        Platform.runLater(() -> {
            boolean offer = false;
            tradeResponseLabel.setText(offeringUser.getUsername() + " offers you: ");
            if (offeringResourceMap.get("brick") > 0) {
                tradeResponseLabel.setText(tradeResponseLabel.getText() + offeringResourceMap.get("brick") + " brick");
                offer = true;
            }
            if (offeringResourceMap.get("ore") > 0) {
                if (offer == true) {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + ", ");
                } else {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + " ");
                }
                tradeResponseLabel.setText(tradeResponseLabel.getText() + offeringResourceMap.get("ore") + " ore");
                offer = true;
            }
            if (offeringResourceMap.get("grain") > 0) {
                if (offer == true) {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + ", ");
                } else {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + " ");
                }
                tradeResponseLabel.setText(tradeResponseLabel.getText() + offeringResourceMap.get("grain") + " grain");
                offer = true;
            }
            if (offeringResourceMap.get("lumber") > 0) {
                if (offer == true) {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + ", ");
                } else {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + " ");
                }
                tradeResponseLabel
                        .setText(tradeResponseLabel.getText() + offeringResourceMap.get("lumber") + " lumber");
                offer = true;
            }
            if (offeringResourceMap.get("wool") > 0) {
                if (offer == true) {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + ", ");
                } else {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + " ");
                }
                tradeResponseLabel.setText(tradeResponseLabel.getText() + offeringResourceMap.get("wool") + " wool");
                offer = true;
            }
            if (offer == false) {
                tradeResponseLabel.setText(tradeResponseLabel.getText() + "nothing");
            }

            boolean response = false;
            tradeResponseLabel.setText(tradeResponseLabel.getText() + " and wants ");

            if (respondingResourceMap.get("brick") > 0) {
                if (response == true) {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + ", ");
                } else {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + " ");
                }
                tradeResponseLabel
                        .setText(tradeResponseLabel.getText() + respondingResourceMap.get("brick") + " brick ");
                response = true;
            }
            if (respondingResourceMap.get("ore") > 0) {
                if (response == true) {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + ", ");
                } else {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + " ");
                }
                tradeResponseLabel.setText(tradeResponseLabel.getText() + respondingResourceMap.get("ore") + " ore ");
                response = true;
            }
            if (respondingResourceMap.get("grain") > 0) {
                if (response == true) {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + ", ");
                } else {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + " ");
                }
                tradeResponseLabel
                        .setText(tradeResponseLabel.getText() + respondingResourceMap.get("grain") + " grain ");
                response = true;
            }
            if (respondingResourceMap.get("lumber") > 0) {
                if (response == true) {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + ", ");
                } else {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + " ");
                }
                tradeResponseLabel
                        .setText(tradeResponseLabel.getText() + respondingResourceMap.get("lumber") + " lumber ");
                response = true;
            }
            if (respondingResourceMap.get("wool") > 0) {
                if (response == true) {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + ", ");
                } else {
                    tradeResponseLabel.setText(tradeResponseLabel.getText() + " ");
                }
                tradeResponseLabel.setText(tradeResponseLabel.getText() + respondingResourceMap.get("wool") + " wool ");
                response = true;
            }
            if (response == false) {
                tradeResponseLabel.setText(tradeResponseLabel.getText() + "nothing");
            }
            tradeResponseLabel.setText(tradeResponseLabel.getText() + ".");
        });
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
