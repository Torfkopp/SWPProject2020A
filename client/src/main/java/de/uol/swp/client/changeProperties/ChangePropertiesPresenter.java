package de.uol.swp.client.changeProperties;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.ClientApp;
import de.uol.swp.client.changeProperties.event.ChangePropertiesCanceledEvent;
import de.uol.swp.client.changeProperties.event.ChangePropertiesSuccessfulEvent;
import de.uol.swp.client.changeProperties.event.SetVolumeErrorEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

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
    public static final int MIN_HEIGHT = 500;
    public static final int MIN_WIDTH = 1300;
    private static final Preferences preferences = Preferences.userNodeForPackage(ClientApp.class);
    @FXML
    private ComboBox<String> themeBox;
    @FXML
    private ComboBox<String> languageBox;
    @FXML
    private ComboBox<String> loginLogoutMsgBox;
    @FXML
    private ComboBox<String> createDeleteLobbyMsgBox;
    @FXML
    private ComboBox<String> joinLeaveLobbyMsgBox;
    @FXML
    private ComboBox<String> ownerRdyNotificationBox;
    @FXML
    private ComboBox<String> ownerTransferNotificationBox;
    @FXML
    private ComboBox<String> soundpackBox;
    @FXML
    private TextField volumeField;
    @FXML
    private TextField backgroundVolumeField;
    @FXML
    private ComboBox<String> gridHitboxesBox;
    @FXML
    private ComboBox<String> loglevelBox;

    @FXML
    private void initialize() {
        themeBox.getItems().setAll("Bars", "Classic", "Cursed", "Dark", "Default", "Palette");
        languageBox.getItems()
                   .addAll("Deutsch - Du", "Deutsch - Sie", "English", "Blind", "Blank", "Hearing-Impaired", "Improved",
                           "lowercase", "UwU Engwish");
        loginLogoutMsgBox.getItems().addAll("On", "Off");
        createDeleteLobbyMsgBox.getItems().addAll("On", "Off");
        joinLeaveLobbyMsgBox.getItems().addAll("On", "Off");
        ownerRdyNotificationBox.getItems().addAll("On", "Off");
        ownerTransferNotificationBox.getItems().addAll("On", "Off");
        soundpackBox.getItems().addAll("Default", "Classic", "Cursed");
        gridHitboxesBox.getItems().addAll("On", "Off");
        loglevelBox.getItems().addAll("ALL", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF");
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
        post(new ChangePropertiesCanceledEvent());
    }

    /**
     * Method called when the ChangePropertiesButton is pressed
     * <p>
     * This method is called when the ChangePropertiesButton is pressed.
     * It gets the value chosen in the ComboBox/TextField and puts it into the Preferences.
     * It also posts a new ChangePropertiesSuccessfulEvent onto the EventBus.
     *
     * @see de.uol.swp.client.changeProperties.event.ChangePropertiesSuccessfulEvent
     */
    @FXML
    private void onChangePropertiesButtonPressed() {
        String theme = themeBox.getValue();
        String language = languageBox.getValue();
        String loginLogoutMsg = loginLogoutMsgBox.getValue();
        String createDeleteLobbyMsg = createDeleteLobbyMsgBox.getValue();
        String joinLeaveLobbyMsg = joinLeaveLobbyMsgBox.getValue();
        String ownerRdyNotification = ownerRdyNotificationBox.getValue();
        String ownerTransferNotification = ownerTransferNotificationBox.getValue();
        String soundpack = soundpackBox.getValue();
        String gridHitboxes = gridHitboxesBox.getValue();
        String loglevel = loglevelBox.getValue();
        if (theme != null) preferences.put("theme", theme.toLowerCase());
        if (language != null) {
            switch (language) {
                case "Deutsch - Du":
                    preferences.put("lang", "de_DE");
                    break;
                case "Deutsch - Sie":
                    preferences.put("lang", "de_DE_sie");
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
                case "lowercase":
                    preferences.put("lang", "en_GB_lowercase");
                    break;
                case "UwU Engwish":
                    preferences.put("lang", "en_GB_UwU");
                    break;
            }
        }
        if (loginLogoutMsg != null) {
            if (loginLogoutMsgBox.getValue().equals("On")) {
                preferences.put("login_logout_msgs_on", "true");
            } else if (loginLogoutMsgBox.getValue().equals("Off")) {
                preferences.put("login_logout_msgs_on", "false");
            }
        }
        if (createDeleteLobbyMsg != null) {
            if (createDeleteLobbyMsgBox.getValue().equals("On")) {
                preferences.put("lobby_create_delete_msgs_on", "true");
            } else if (createDeleteLobbyMsgBox.getValue().equals("Off")) {
                preferences.put("lobby_create_delete_msgs_on", "false");
            }
        }
        if (joinLeaveLobbyMsg != null) {
            if (joinLeaveLobbyMsgBox.getValue().equals("On")) {
                preferences.put("join_leave_msgs_on", "true");
            } else if (joinLeaveLobbyMsgBox.getValue().equals("Off")) {
                preferences.put("join_leave_msgs_on", "false");
            }
        }
        if (ownerRdyNotification != null) {
            if (ownerRdyNotificationBox.getValue().equals("On")) {
                preferences.put("owner_ready_notifs_on", "true");
            } else if (ownerRdyNotificationBox.getValue().equals("Off")) {
                preferences.put("owner_ready_notifs_on", "false");
            }
        }
        if (ownerTransferNotification != null) {
            if (ownerTransferNotificationBox.getValue().equals("On")) {
                preferences.put("owner_transfer_notifs_on", "true");
            } else if (ownerTransferNotificationBox.getValue().equals("Off")) {
                preferences.put("owner_transfer_notifs_on", "false");
            }
        }
        if (soundpack != null) preferences.put("soundpack", soundpack.toLowerCase());
        if (!volumeField.getText().equals("")) {
            try {
                int volume = Integer.parseInt(volumeField.getText());
                if (volume < 0 || volume > 100) {
                    post(new SetVolumeErrorEvent(resourceBundle.getString("changeproperties.error.volume")));
                } else {
                    preferences.put("volume", Integer.toString(volume));
                }
            } catch (NumberFormatException ignored) {
                post(new SetVolumeErrorEvent(resourceBundle.getString("changeproperties.error.volume")));
                return;
            }
        }
        if (!backgroundVolumeField.getText().equals("")) {
            try {
                int backgroundVolume = Integer.parseInt(backgroundVolumeField.getText());
                if (backgroundVolume < 0 || backgroundVolume > 100) {
                    post(new SetVolumeErrorEvent(resourceBundle.getString("changeptoperties.error.backgroundvolume")));
                } else {
                    preferences.put("backgroundvolume", Integer.toString(backgroundVolume));
                }
            } catch (NumberFormatException ignored) {
                post(new SetVolumeErrorEvent(resourceBundle.getString("changeptoperties.error.backgroundvolume")));
                return;
            }
        }
        if (gridHitboxes != null) {
            if (gridHitboxesBox.getValue().equals("On")) {
                preferences.put("debug.draw_hitbox_grid", "true");
            } else if (gridHitboxesBox.getValue().equals("Off")) {
                preferences.put("debug.draw_hitbox_grid", "false");
            }
        }
        if (loglevel != null) preferences.put("debug.loglevel", loglevel);
        post(new ChangePropertiesSuccessfulEvent());
    }
}
