package de.uol.swp.client.trade;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

import java.util.Map;

/**
 * An AbstractPresenter for all TradePresenters
 *
 * @author Mario Fokken
 * @since 2021-04-16
 */
@SuppressWarnings("rawtypes")
public class AbstractTradePresenter extends AbstractPresenter {

    @Inject
    protected ITradeService tradeService;

    @FXML
    protected TableView<Map<String, Object>> ownResourceTableView;
    // MapValueFactory doesn't support specifying a Map's generics, so the Map type is used raw here (Warning suppressed)
    @FXML
    protected TableColumn<Map, Integer> resourceAmountCol;
    @FXML
    protected TableColumn<Map, String> resourceNameCol;

    /**
     * Initialises the Presenters by setting up the ownResourceTableView
     *
     * @implNote Called automatically by JavaFX
     */
    @FXML
    public void initialize() {
        resourceAmountCol.setCellValueFactory(new MapValueFactory<>("amount"));
        resourceNameCol.setCellValueFactory(new MapValueFactory<>("resource"));
    }
}
