package de.uol.swp.client.changeSettings;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.ClientApp;
import de.uol.swp.client.changeSettings.event.ChangedGameSettingsEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.prefs.Preferences;

/**
 * Manages the ChangeGameSettings menu
 *
 * @author Marvin Drees
 * @since 2021-06-14
 */
public class ChangeGameSettingsPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/ChangeGameSettingsView.fxml";
    public static final int MIN_HEIGHT = 400;
    public static final int MIN_WIDTH = 1000;
    private static final Preferences preferences = Preferences.userNodeForPackage(ClientApp.class);
    @FXML
    private ComboBox<String> renderingStyleBox;

    @FXML
    private void initialize() {
        renderingStyleBox.getItems().setAll("Plain", "Pixel");
    }

    /**
     * Method called when the CancelButton is pressed
     * <p>
     * This method is called when the CancelButton is pressed and just
     * closes the ChangeGameSettingsView.
     */
    @FXML
    private void onCancelButtonPressed() {
        soundService.button();
        renderingStyleBox.getScene().getWindow().hide();
    }

    /**
     * Method called when the ChangeGameSettingsButton is pressed
     * <p>
     * This method is called when the ChangeGameSettingsButton is pressed.
     * It gets the value chosen in the ComboBox and puts it into the Preferences.
     * It also posts a new ChangeDGameSettingsEvent onto the EventBus.
     */
    @FXML
    private void onChangeGameSettingsButtonPressed() {
        soundService.button();
        String theme = renderingStyleBox.getValue();
        if (theme != null) {
            preferences.put("renderingstyle", theme.toLowerCase());
            post(new ChangedGameSettingsEvent(theme.toLowerCase()));
        }
        renderingStyleBox.getScene().getWindow().hide();
    }
}
