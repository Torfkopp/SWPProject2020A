package de.uol.swp.client.auth;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.auth.events.RetryLoginEvent;
import de.uol.swp.client.register.event.ShowRegistrationViewEvent;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final ShowRegistrationViewEvent showRegViewMessage = new ShowRegistrationViewEvent();

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
     * @see de.uol.swp.client.register.event.ShowRegistrationViewEvent
     * @see de.uol.swp.client.SceneManager
     * @since 2019-08-13
     */
    @FXML
    private void onRegisterButtonPressed() {
        eventBus.post(showRegViewMessage);
    }

    /**
     * Method called when RetryLoginEvent received
     * <p>
     * This method gets called when a RetryLoginEvent is detected on
     * the eventbus. It behaves as if the login button is pressed.
     *
     * @author Eric Vuong
     * @author Marvin Drees
     * @see de.uol.swp.client.auth.events.RetryLoginEvent
     * @since 2021-03-04
     */
    @Subscribe
    private void onRetryLoginEvent(RetryLoginEvent event) {
        LOG.debug("Received RetryLoginEvent");
        onLoginButtonPressed();
    }
}
