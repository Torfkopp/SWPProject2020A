package de.uol.swp.client.trade;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TradeWithUserPresenter extends AbstractPresenter {
    public static final String fxml = "/fxml/TradeWithUserView.fxml";
    private final Logger LOG = LogManager.getLogger(TradeWithUserPresenter.class);
    private Window window;
    @FXML
    private Button cancelTradeButton;

    @Inject
    public TradeWithUserPresenter(EventBus eventBus) {
        setEventBus(eventBus);
    }

    public void onCancelTradeButtonPressed(ActionEvent actionEvent) {
        eventBus.post(new TradeWithUserCancelEvent("a"));
        //todo TradeWithUserPresenter benötigt Lobbynamen
        //todo TradeButton in Lobbyview sichtbar machen
    }
}
