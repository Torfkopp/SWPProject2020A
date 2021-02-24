package de.uol.swp.client.trade;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.trade.event.TradeWithUserResponseUpdateEvent;
import de.uol.swp.common.game.message.TradeWithUserOfferMessage;
import de.uol.swp.common.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("ALL")
public class TradeWithUserAcceptPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/TradeWithUserAcceptView.fxml";
    private final Logger LOG = LogManager.getLogger(TradeWithUserAcceptPresenter.class);
    @FXML
    private Label tradeResponseLabel;
    private User offeringUser;
    private String respondingUser;
    private String lobbyName;
    @FXML
    private Button rejectTradeButton;
    @FXML
    private Button acceptTradeButton;

    @Inject
    public TradeWithUserAcceptPresenter(EventBus eventBus) {
        setEventBus(eventBus);
    }

    @FXML
    public void initialize() {

    }

    @FXML
    private void onAcceptTradeButtonPressed() {
    }

    @FXML
    private void onRejectTradeButtonPressed() {
    }

    @Subscribe
    private void onTradeWithUserResponseUpdateEvent(TradeWithUserResponseUpdateEvent event) {
        //todo Fenster nur bei responding user anzeigen
        offeringUser = event.getOfferingUser();
            respondingUser = event.getResponseUser();
            lobbyName = event.getLobbyName();
            setLabel();
    }

    private void setLabel() {
        LOG.debug("Setting the Label");
        tradeResponseLabel.setText(offeringUser.getUsername() + "bietet dir x an");
    }

    @Subscribe
    private void onTradeWithUserOfferMessage(TradeWithUserOfferMessage message){
        System.out.println("MOOOOIN");
    }
}
