package de.uol.swp.client.trade;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Pair;

/**
 * An AbstractPresenter for all TradePresenters
 *
 * @author Mario Fokken
 * @since 2021-04-16
 */
public class AbstractTradePresenter extends AbstractPresenter {

    @Inject
    protected ITradeService tradeService;

    @FXML
    protected ListView<Pair<String, Integer>> ownInventoryView;

    /**
     * Initialises the Presenters by setting up the ownInventoryView
     *
     * @implNote Called automatically by JavaFX
     */
    @FXML
    public void initialize() {
        ownInventoryView.setCellFactory(lv -> getListCell());
    }

    /**
     * A CellFactory for the all views used in trade
     *
     * @return A ListCell
     */
    protected ListCell<Pair<String, Integer>> getListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Pair<String, Integer> item, boolean empty) {
                Platform.runLater(() -> {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" :
                            item.getValue().toString() + " " + resourceBundle.getString(item.getKey()));
                });
            }
        };
    }
}
