package de.uol.swp.client.register;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.register.event.RegistrationCanceledEvent;
import de.uol.swp.client.register.event.RegistrationErrorEvent;
import de.uol.swp.client.user.ClientUserService;
import de.uol.swp.common.user.UserDTO;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Manages the registration window
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2019-08-29
 */
@SuppressWarnings("UnstableApiUsage")
public class RegistrationPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/RegistrationView.fxml";
    private static final RegistrationCanceledEvent registrationCanceledEvent = new RegistrationCanceledEvent();

    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField1;
    @FXML
    private PasswordField passwordField2;

    /**
     * Default Constructor
     *
     * @since 2019-09-18
     */
    public RegistrationPresenter() {
    }

    /**
     * Constructor
     *
     * @param eventBus    The EventBus set in ClientModule
     * @param userService The injected ClientUserService
     *
     * @see de.uol.swp.client.di.ClientModule
     * @since 2019-09-18
     */
    @Inject
    public RegistrationPresenter(EventBus eventBus, ClientUserService userService) {
        setEventBus(eventBus);
    }

    /**
     * Method called when the button to cancel the process is pressed
     * <p>
     * This Method is called when the CancelButton is pressed. It posts an instance
     * of the RegistrationCanceledEvent onto the EventBus the SceneManager is subscribed
     * to.
     *
     * @see de.uol.swp.client.register.event.RegistrationCanceledEvent
     * @see de.uol.swp.client.SceneManager
     * @since 2019-09-02
     */
    @FXML
    private void onCancelButtonPressed() {
        eventBus.post(registrationCanceledEvent);
    }

    /**
     * Method called when the button to register is pressed
     * <p>
     * This Method is called when the RegisterButton is pressed. It posts an instance
     * of the RegistrationErrorEvent onto the EventBus the SceneManager is subscribed
     * to if one of the fields is empty or the password fields are not equal.
     * If everything is filled in correctly, the user service is requested to create
     * a new user.
     *
     * @see de.uol.swp.client.register.event.RegistrationErrorEvent
     * @see de.uol.swp.client.SceneManager
     * @see de.uol.swp.client.user.UserService
     * @since 2019-09-02
     */
    @FXML
    private void onRegisterButtonPressed() {
        if (Strings.isNullOrEmpty(loginField.getText())) {
            eventBus.post(new RegistrationErrorEvent(resourceBundle.getString("register.error.empty.username")));
        } else if (!passwordField1.getText().equals(passwordField2.getText())) {
            eventBus.post(new RegistrationErrorEvent(resourceBundle.getString("register.error.notequalpw")));
        } else if (Strings.isNullOrEmpty(passwordField1.getText())) {
            eventBus.post(new RegistrationErrorEvent(resourceBundle.getString("register.error.empty.password")));
        } else {
            userService.createUser(new UserDTO(loginField.getText(), passwordField1.getText(), ""));
        }
    }
}
