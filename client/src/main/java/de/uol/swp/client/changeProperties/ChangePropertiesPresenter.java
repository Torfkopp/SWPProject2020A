package de.uol.swp.client.changeProperties;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.ClientApp;
import de.uol.swp.client.changeProperties.event.ChangePropertiesCanceledEvent;
import de.uol.swp.client.changeProperties.event.ChangePropertiesSuccessfulEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.prefs.Preferences;

/**
 * Manages the changeProperties menu
 *
 * @author Alwin Bossert
 * @since 2021-05-22
 */
@SuppressWarnings("UnstableApiUsage")
public class ChangePropertiesPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/ChangePropertiesView.fxml";
    public static final int MIN_HEIGHT = 390;
    public static final int MIN_WIDTH = 395;
    private static final Preferences preferences = Preferences.userNodeForPackage(ClientApp.class);
    @FXML
    private ComboBox<String> themeBox;
    @FXML
    private ComboBox<String> languageBox;

    @FXML
    private void initialize() {
        themeBox.getItems().setAll("Bars", "Classic", "Cursed", "Dark", "Default", "Palette");
        languageBox.getItems()
                   .addAll("German", "English", "Blind", "Blank", "Hearing-Impaired", "Improved", "Lowercase", "UwU");
    }

    /**
     * Method called when the CancelButton is pressed
     * <p>
     * This method is called when the CancelButton is pressed.
     * It posts a new ChangePropertiesCanceledEvent onto the EventBus.
     *
     * @see de.uol.swp.client.changeProperties.event.ChangePropertiesCanceledEvent
     */
    @FXML
    private void onCancelButtonPressed() {
        eventBus.post(new ChangePropertiesCanceledEvent());
    }

    /**
     * Method called when the ChangePropertiesButton is pressed
     * <p>
     * This method is called when the ChangePropertiesButton is pressed.
     * It gets the value chosen in the ComboBox and puts it into the Preferences.
     * It also posts a new ChangePropertiesSuccessfulEvent onto the EventBus
     *
     * @see de.uol.swp.client.changeProperties.event.ChangePropertiesSuccessfulEvent
     */
    @FXML
    private void onChangePropertiesButtonPressed() {
        String theme = themeBox.getValue();
        String language = languageBox.getValue();
        if (theme != null) preferences.put("theme", theme.toLowerCase());
        if (language != null) {
            switch (language) {
                case "German":
                    preferences.put("lang", "de_DE");
                    break;
                case "English":
                    preferences.put("lang", "en_GB");
                    break;
                case "Blind":
                    preferences.put("lang", "en_GB_blind");
                    break;
                case "Blank":
                    preferences.put("lang", "en_GB_blank");
                    break;
                case "Hearing-Impaired":
                    preferences.put("lang", "en_GB_hearing_impaired");
                    break;
                case "Improved":
                    preferences.put("lang", "en_GB_improved");
                    break;
                case "Lowercase":
                    preferences.put("lang", "en_GB_lowercase");
                    break;
                case "UwU":
                    preferences.put("lang", "en_GB_UwU");
                    break;
            }
        }
        eventBus.post(new ChangePropertiesSuccessfulEvent());
    }
}
