package de.uol.swp.client.trade;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TradeWithUserPresenter extends AbstractPresenter {
    public static final String fxml = "/fxml/TradeWithUserView.fxml";
    private final Logger LOG = LogManager.getLogger(TradeWithUserPresenter.class);

    @Inject
    public TradeWithUserPresenter(EventBus eventBus) {
        setEventBus(eventBus);
    }
}
