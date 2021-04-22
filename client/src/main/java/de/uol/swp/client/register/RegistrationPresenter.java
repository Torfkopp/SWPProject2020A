package de.uol.swp.client.register;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.register.event.RegistrationCanceledEvent;
import de.uol.swp.client.register.event.RegistrationErrorEvent;
import de.uol.swp.common.user.UserDTO;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static final int MIN_HEIGHT = 250;
    public static final int MIN_WIDTH = 410;
    private static final RegistrationCanceledEvent registrationCanceledEvent = new RegistrationCanceledEvent();
    private final Logger LOG = LogManager.getLogger(RegistrationPresenter.class);

    @FXML
    private TextField loginField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField1;
    @FXML
    private PasswordField passwordField2;
    @FXML
    private Button registerButton;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     *
     * @see de.uol.swp.client.di.ClientModule
     * @since 2019-09-18
     */
    @Inject
    public RegistrationPresenter(EventBus eventBus) {
        setEventBus(eventBus);
    }

    @FXML
    protected void initialize() {
        prepareLoginFormat();
    }

    /**
     * Method called to compare the eMail string with valid regex
     * <p>
     * This helper method is called to compare whether a provided
     * string complies to RFC5322 and some other restrictions like
     * to adjacent dots. If it matches, the boolean true is returned.
     *
     * @param eMail the mail string provided during registration
     *
     * @author Aldin Dervisi
     * @author Marvin Drees
     * @since 2021-02-25
     */
    private boolean checkMailFormat(String eMail) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(eMail);

        return matcher.matches();
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
     * to if one of the fields is empty, the password fields are not equal, or the
     * eMail field is malformed.
     * If everything is filled in correctly, the user service is requested to create
     * a new user.
     *
     * @author Aldin Dervisi
     * @author Marvin Drees
     * @implNote The ID of the User to create is set to -1.
     * This will NOT be its final ID and must not be used for identification in any way
     * @see de.uol.swp.client.register.event.RegistrationErrorEvent
     * @see de.uol.swp.client.SceneManager
     * @see de.uol.swp.client.user.UserService
     * @since 2021-02-25
     */
    @FXML
    private void onRegisterButtonPressed() {
        if (Strings.isNullOrEmpty(loginField.getText())) {
            eventBus.post(new RegistrationErrorEvent(resourceBundle.getString("register.error.empty.username")));
        } else if (!checkMailFormat(emailField.getText())) {
            eventBus.post(new RegistrationErrorEvent(resourceBundle.getString("register.error.invalid.email")));
        } else if (!passwordField1.getText().equals(passwordField2.getText())) {
            eventBus.post(new RegistrationErrorEvent(resourceBundle.getString("register.error.notequalpw")));
        } else if (Strings.isNullOrEmpty(passwordField1.getText())) {
            eventBus.post(new RegistrationErrorEvent(resourceBundle.getString("register.error.empty.password")));
        } else {
            userService.createUser(new UserDTO(-1, loginField.getText(), userService.hash(passwordField1.getText()),
                                               emailField.getText()));
        }
    }

    /**
     * Prepares the loginField
     * Helper method, called when the Registrationpresenter is initialised in order to let the loginField
     * only accept alphanumeric entries with the addition of underscore and hyphen
     *
     * @author Sven Ahrens
     * @since 2021-04-21
     */
    private void prepareLoginFormat() {
        UnaryOperator<TextFormatter.Change> StringFilter = (s) ->
                s.getText().matches("[A-Za-z0-9_-]+") || s.isDeleted() || s.getText().equals("") ? s : null;
        loginField.setTextFormatter(new TextFormatter<>(StringFilter));
        //@formatter:off
        registerButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            boolean name = loginField.getText().isBlank() || !loginField.getText().matches("[A-Za-z0-9_-]+");
            boolean mail = emailField.getText().isBlank() || !checkMailFormat(emailField.getText());
            boolean password = passwordField1.getText().isBlank()
                               || !passwordField1.getText().equals(passwordField2.getText())
                               || passwordField2.getText().isBlank();
            return name || mail || password;
            }, loginField.textProperty(), emailField.textProperty(), passwordField1.textProperty(), passwordField2.textProperty()));
        //@formatter:on
    }
}
