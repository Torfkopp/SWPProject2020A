package de.uol.swp.client.ChangePassword;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.ChangePassword.event.ChangePasswordCanceledEvent;
import de.uol.swp.client.ChangePassword.event.ChangePasswordErrorEvent;
import de.uol.swp.client.user.ClientUserService;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the ChangePassword window
 *
 * @author Eric Vuong
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2020-11-25
 */
@SuppressWarnings("UnstableApiUsage")
public class ChangePasswordPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/ChangePasswordView.fxml";
    private static final Logger LOG = LogManager.getLogger(ChangePasswordPresenter.class);
    private static final ChangePasswordCanceledEvent ChangePasswordCanceledEvent = new ChangePasswordCanceledEvent();

    @FXML
    private PasswordField OldPasswordField1;
    @FXML
    private PasswordField NewPasswordField1;
    @FXML
    private PasswordField NewPasswordField2;

    /**
     * Default Constructor
     */
    public ChangePasswordPresenter() {
    }

    /**
     * Constructor
     *
     * @param eventBus    The EventBus set in ClientModule
     * @param userService The injected ClientUserService
     *
     * @author Eric Vuong
     * @author Steven Luong
     * @see de.uol.swp.client.di.ClientModule
     * @since 2020-11-25
     */
    @Inject
    public ChangePasswordPresenter(EventBus eventBus, ClientUserService userService) {
        setEventBus(eventBus);
        LOG.debug("ChangePasswordPresenter was started");
    }

    /**
     * Method called when the button to cancel the process is pressed
     * <p>
     * This Method is called when the CancelButton is pressed. It posts an instance
     * of the ChangePasswordCanceledEvent onto the EventBus the SceneManager is subscribed
     * to.
     *
     * @author Eric Vuong
     * @see de.uol.swp.client.ChangePassword.event.ChangePasswordCanceledEvent
     * @see de.uol.swp.client.SceneManager
     * @since 2020-11-25
     */
    @FXML
    private void onCancelButtonPressed() {
        eventBus.post(ChangePasswordCanceledEvent);
    }

    /**
     * Method called when the button to change the password is pressed
     * <p>
     * This Method is called when the ChangePasswordButton is pressed. It posts an instance
     * of the ChangePasswordErrorEvent onto the EventBus the SceneManager is subscribed
     * to if one of the fields is empty or the password fields are not equal.
     * If everything is filled in correctly, the user service is requested to update
     * the user's password.
     *
     * @author Eric Vuong
     * @author Steven Luong
     * @see de.uol.swp.client.ChangePassword.event.ChangePasswordErrorEvent
     * @see de.uol.swp.client.SceneManager
     * @see de.uol.swp.client.user.UserService
     * @since 2020-12-05
     */
    @FXML
    private void onChangePasswordButtonPressed() {
        //the UserData is set in the showChangePasswordScreen Method in the SceneManager
        User user = (User) OldPasswordField1.getScene().getUserData();

        if (Strings.isNullOrEmpty(OldPasswordField1.getText())) {
            eventBus.post(new ChangePasswordErrorEvent("Old Password cannot be empty"));
        } else if (Strings.isNullOrEmpty(NewPasswordField1.getText())) {
            eventBus.post(new ChangePasswordErrorEvent("New Password cannot be empty"));
        } else if (Strings.isNullOrEmpty(NewPasswordField2.getText())) {
            eventBus.post(new ChangePasswordErrorEvent("Repeat new Password cannot be empty"));
        } else if (!NewPasswordField1.getText().equals(NewPasswordField2.getText())) {
            eventBus.post(new ChangePasswordErrorEvent("Your New Passwords are not equal"));
        } else {
            userService.updateUserPassword(new UserDTO(user.getUsername(), NewPasswordField1.getText(), "empty"),
                                           OldPasswordField1.getText());
        }
    }
}
