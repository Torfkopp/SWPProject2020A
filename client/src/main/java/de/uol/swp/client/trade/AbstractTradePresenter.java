package de.uol.swp.client.trade;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.IResource;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
    protected TableView<IResource> ownResourceTableView;
    @FXML
    protected TableColumn<IResource, Integer> resourceAmountCol;
    @FXML
    protected TableColumn<IResource, ResourceType> resourceNameCol;

    /**
     * Initialises the Presenters by setting up the ownResourceTableView
     *
     * @implNote Called automatically by JavaFX
     */
    @FXML
    public void initialize() {
        resourceAmountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        resourceNameCol.setCellValueFactory(new PropertyValueFactory<>("type"));
    }
}
