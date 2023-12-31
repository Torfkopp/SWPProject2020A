package de.uol.swp.client.auth;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.auth.event.RetryLoginEvent;
import de.uol.swp.client.scene.event.SetAcceleratorsEvent;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the login window
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2019-08-08
 */
@SuppressWarnings("UnstableApiUsage")
public class LoginPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/LoginView.fxml";
    public static final int MIN_HEIGHT = 220;
    public static final int MIN_WIDTH = 400;
    private static final Logger LOG = LogManager.getLogger(LoginPresenter.class);

    @FXML
    private Button loginButton;
    @FXML
    private CheckBox rememberMeCheckbox;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField loginField;

    @FXML
    private void initialize() {
        loginButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            boolean nameInvalid = loginField.getText().isEmpty() || !loginField.getText().matches("[A-Za-z0-9_-]+");
            boolean passwordInvalid = passwordField.getText().isEmpty();
            return nameInvalid || passwordInvalid;
        }, loginField.textProperty(), passwordField.textProperty()));
        LOG.debug("LoginPresenter initialised");
    }

    /**
     * Method called when the login button is pressed
     * <p>
     * This Method is called when the login button is pressed. It takes the text
     * entered in the login and password field, and gives the user service a request
     * to log in the user specified by those fields. It will also only enable the login
     * button if the entered username is valid and the password field is not empty.
     *
     * @see de.uol.swp.client.user.UserService
     * @since 2019-08-13
     */
    @FXML
    private void onLoginButtonPressed() {
        if (loginButton.isDisabled()) {
            LOG.trace("onLoginButtonPressed called with disabled button, returning");
            return;
        }
        soundService.button();
        userService.login(loginField.getText(), userService.hash(passwordField.getText()),
                          rememberMeCheckbox.isSelected());
    }

    /**
     * Method called when the register button is pressed
     * <p>
     * This Method is called when the register button is pressed. It posts an instance
     * of the ShowRegistrationViewEvent onto the EventBus the SceneManager is subscribed
     * to.
     *
     * @see de.uol.swp.client.scene.SceneManager
     * @since 2019-08-13
     */
    @FXML
    private void onRegisterButtonPressed() {
        soundService.button();
        sceneService.displayRegistrationScreen();
    }

    /**
     * Method called when RetryLoginEvent received
     * <p>
     * This method gets called when a RetryLoginEvent is detected on
     * the eventbus. It behaves as if the login button is pressed.
     *
     * @author Eric Vuong
     * @author Marvin Drees
     * @see de.uol.swp.client.auth.event.RetryLoginEvent
     * @since 2021-03-04
     */
    @Subscribe
    private void onRetryLoginEvent(RetryLoginEvent event) {
        LOG.debug("Received RetryLoginEvent");
        Platform.runLater(this::onLoginButtonPressed);
    }

    /**
     * Handles a SetAcceleratorEvent found on the EventBus
     * <p>
     * This method sets the accelerators for the LoginPresenter, namely
     * <ul>
     *     <li> CTRL/META + L = Click Login button
     *     <li> CTRL/META + R = Click Register button
     *     <li> CTRL/META + M = Toggle 'Remember Me' checkbox
     *
     * @param event The SetAcceleratorEvent found on the EventBus
     *
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.scene.event.SetAcceleratorsEvent
     * @since 2021-05-20
     */
    @Subscribe
    private void onSetAcceleratorsEvent(SetAcceleratorsEvent event) {
        LOG.debug("Received SetAcceleratorsEvent");
        Map<KeyCombination, Runnable> accelerators = new HashMap<>();
        accelerators.put(new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN), // CTRL/META + L
                         this::onLoginButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN), // CTRL/META + R
                         this::onRegisterButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.M, KeyCombination.SHORTCUT_DOWN), // CTRL/META + M
                         () -> rememberMeCheckbox.setSelected(!rememberMeCheckbox.isSelected()));
        loginButton.getScene().getAccelerators().putAll(accelerators);
    }
}
