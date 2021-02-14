package de.uol.swp.client.auth;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.register.event.ShowRegistrationViewEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
    private static final Logger LOG = LogManager.getLogger(LoginPresenter.class);
    private static final ShowRegistrationViewEvent showRegViewMessage = new ShowRegistrationViewEvent();

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField loginField;

    /**
     * Default Constructor
     *
     * @since 2019-08-18
     */
    public LoginPresenter() {
        LOG.debug("LoginPresenter started");
    }

    /**
     * Method called when the login button is pressed
     * <p>
     * This Method is called when the login button is pressed. It takes the text
     * entered in the login and password field, and gives the user service a request
     * to log in the user specified by those fields.
     *
     * @see de.uol.swp.client.user.UserService
     * @since 2019-08-13
     */
    @FXML
    private void onLoginButtonPressed() {
        userService.login(loginField.getText(), passwordField.getText());
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
}
