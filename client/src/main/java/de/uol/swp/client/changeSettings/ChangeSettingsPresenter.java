package de.uol.swp.client.changeSettings;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.ClientApp;
import de.uol.swp.common.util.ResourceManager;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.prefs.Preferences;

/**
 * Manages the changeSettings menu
 *
 * @author Alwin Bossert
 * @since 2021-05-22
 */
public class ChangeSettingsPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/ChangeSettingsView.fxml";
    public static final int MIN_HEIGHT = 400;
    public static final int MIN_WIDTH = 1000;
    private static final Preferences preferences = Preferences.userNodeForPackage(ClientApp.class);
    @FXML
    private ComboBox<String> themeBox;
    @FXML
    private ComboBox<String> languageBox;
    @FXML
    private CheckBox loginLogoutMsgBox;
    @FXML
    private CheckBox createDeleteLobbyMsgBox;
    @FXML
    private CheckBox joinLeaveLobbyMsgBox;
    @FXML
    private CheckBox ownerRdyNotificationBox;
    @FXML
    private CheckBox ownerTransferNotificationBox;
    @FXML
    private ComboBox<String> soundpackBox;
    @FXML
    private TextField volumeField;
    @FXML
    private TextField backgroundVolumeField;
    @FXML
    private CheckBox gridHitboxesBox;
    @FXML
    private ComboBox<String> loglevelBox;

    @FXML
    private void initialize() {
        themeBox.getItems().setAll("Classic", "Cursed", "Dark", "Default");
        languageBox.getItems().addAll("English", "Deutsch (Du)", "Deutsch (kein Unicode)", "Deutsch (Sie)", " ", "⠃⠇⠊⠝⠙",
                                      "HEARING-IMPAIRED", "Improved", "lowercase", "No Unicode", "Playing Cards",
                                      "Psэuбф Cчяilliс", "uʍop ǝpᴉsd∩", "UwU", "UWU", "uwu", "Plattdütsk",
                                      "PLATT FÖR OLLE LÜÜ");
        loginLogoutMsgBox.setSelected(Boolean.parseBoolean(preferences.get("login_logout_msgs_on", "")));
        createDeleteLobbyMsgBox.setSelected(Boolean.parseBoolean(preferences.get("lobby_create_delete_msgs_on", "")));
        joinLeaveLobbyMsgBox.setSelected(Boolean.parseBoolean(preferences.get("join_leave_msgs_on", "")));
        ownerRdyNotificationBox.setSelected(Boolean.parseBoolean(preferences.get("owner_ready_notifs_on", "")));
        ownerTransferNotificationBox.setSelected(Boolean.parseBoolean(preferences.get("owner_transfer_notifs_on", "")));
        soundpackBox.getItems().addAll("Default", "Classic", "Cursed");
        gridHitboxesBox.setSelected(Boolean.parseBoolean(preferences.get("debug.draw_hitbox_grid", "")));
        loglevelBox.getItems().addAll("ALL", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF");
    }

    /**
     * Method called when the CancelButton is pressed
     * <p>
     * This method is called when the CancelButton is pressed.
     * It posts a new ChangeSettingsCanceledEvent onto the EventBus.
     */
    @FXML
    private void onCancelButtonPressed() {
        soundService.button();
        sceneService.displayMainMenuScreen();
    }

    /**
     * Method called when the ChangeSettingsButton is pressed
     * <p>
     * This method is called when the ChangeSettingsButton is pressed.
     * It gets the value chosen in the ComboBox/TextField and puts it into the Preferences.
     * It also posts a new ChangeSettingsSuccessfulEvent onto the EventBus.
     */
    @FXML
    private void onChangeSettingsButtonPressed() {
        soundService.button();
        String theme = themeBox.getValue();
        String language = languageBox.getValue();
        String soundpack = soundpackBox.getValue();
        String loglevel = loglevelBox.getValue();
        if (theme != null) preferences.put("theme", theme.toLowerCase());
        if (language != null) {
            switch (language) {
                case "English":
                    preferences.put("lang", "en_GB");
                    break;

                case "Deutsch (Du)":
                    preferences.put("lang", "de_DE");
                    break;
                case "Deutsch (kein Unicode)":
                    preferences.put("lang", "de_DE_nounicode");
                    break;
                case "Deutsch (Sie)":
                    preferences.put("lang", "de_DE_sie");
                    break;

                case " ":
                    preferences.put("lang", "en_GB_blank");
                    break;
                case "⠃⠇⠊⠝⠙":
                    preferences.put("lang", "en_GB_blind");
                    break;
                case "HEARING-IMPAIRED":
                    preferences.put("lang", "en_GB_hearing_impaired");
                    break;
                case "Improved":
                    preferences.put("lang", "en_GB_improved");
                    break;
                case "lowercase":
                    preferences.put("lang", "en_GB_lowercase");
                    break;
                case "No Unicode":
                    preferences.put("lang", "en_GB_nounicode");
                    break;
                case "Playing Cards":
                    preferences.put("lang", "en_GB_playing-cards");
                    break;
                case "Psэuбф Cчяilliс":
                    preferences.put("lang", "en_GB_pseudo-cyrillic");
                    break;
                case "uʍop ǝpᴉsd∩":
                    preferences.put("lang", "en_GB_upside-down");
                    break;

                case "UwU":
                    preferences.put("lang", "en_GB_UwU");
                    break;
                case "UWU":
                    preferences.put("lang", "en_GB_uwu-scweam");
                    break;
                case "uwu":
                    preferences.put("lang", "en_GB_uwu-wowewcase");
                    break;

                case "Plattdütsk":
                    preferences.put("Lang", "nds_DE");
                case "PLATT FÖR OLLE LÜÜ":
                    preferences.put("Lang", "nds_DE_for-olle-luu");
            }
        }
        preferences.putBoolean("login_logout_msgs_on", loginLogoutMsgBox.isSelected());
        preferences.putBoolean("lobby_create_delete_msgs_on", createDeleteLobbyMsgBox.isSelected());
        preferences.putBoolean("join_leave_msgs_on", joinLeaveLobbyMsgBox.isSelected());
        preferences.putBoolean("owner_ready_notifs_on", ownerRdyNotificationBox.isSelected());
        preferences.putBoolean("owner_transfer_notifs_on", ownerTransferNotificationBox.isSelected());
        if (soundpack != null) preferences.put("soundpack", soundpack.toLowerCase());
        if (!volumeField.getText().equals("")) {
            try {
                int volume = Integer.parseInt(volumeField.getText());
                if (volume < 0 || volume > 100) {
                    sceneService.showError(ResourceManager.get("changesettings.error.volume"));
                } else {
                    preferences.put("volume", Integer.toString(volume));
                }
            } catch (NumberFormatException ignored) {
                sceneService.showError(ResourceManager.get("changesettings.error.volume"));
                return;
            }
        }
        if (!backgroundVolumeField.getText().equals("")) {
            try {
                int backgroundVolume = Integer.parseInt(backgroundVolumeField.getText());
                if (backgroundVolume < 0 || backgroundVolume > 100) {
                    sceneService.showError(ResourceManager.get("changesettings.error.backgroundvolume"));
                } else {
                    preferences.put("backgroundvolume", Integer.toString(backgroundVolume));
                }
            } catch (NumberFormatException ignored) {
                sceneService.showError(ResourceManager.get("changesettings.error.backgroundvolume"));
                return;
            }
        }
        preferences.putBoolean("debug.draw_hitbox_grid", gridHitboxesBox.isSelected());
        if (loglevel != null) {
            preferences.put("debug.loglevel", loglevel);
        }
        sceneService.displayMainMenuScreen();
    }
}
