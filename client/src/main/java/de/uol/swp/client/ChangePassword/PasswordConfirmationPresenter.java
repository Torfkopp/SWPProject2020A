package de.uol.swp.client.ChangePassword;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.ChangePassword.event.ConfirmPasswordCanceledEvent;
import de.uol.swp.client.ChangePassword.event.ConfirmPasswordErrorEvent;
import de.uol.swp.common.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

/**
 * Manages the ConfirmPassword window
 *
 * @author Eric Vuong
 * @author Alwin Bossert
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-03-16
 */
public class PasswordConfirmationPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/PasswordConfirmationView.fxml";
    private static final ConfirmPasswordCanceledEvent confirmPasswordCanceledEvent = new ConfirmPasswordCanceledEvent();

    @FXML
    private PasswordField ConfirmPassword;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     *
     * @author Eric Vuong
     * @author Alwin Bossert
     * @see de.uol.swp.client.di.ClientModule
     * @since 2021-03-16
     */
    @Inject
    public PasswordConfirmationPresenter(EventBus eventBus) {
        setEventBus(eventBus);
    }

    /**
     * Method called when the button to confirm the password is pressed
     * <p>
     * This Method is called when the ConfirmPasswordButton is pressed. It posts an instance
     * of the ConfirmPasswordErrorEvent onto the EventBus the SceneManager is subscribed
     * to if the field is empty.
     * If everything is filled in correctly, the user service is requested to confirm
     * the user's password.
     *
     * @author Eric Vuong
     * @author Alwin Bossert
     * @see de.uol.swp.client.ChangePassword.event.ChangePasswordErrorEvent
     * @see de.uol.swp.client.SceneManager
     * @see de.uol.swp.client.user.UserService
     * @since 2021-03-16
     */
    @FXML
    public void onConfirmPasswordButtonPressed() {
        //the UserData is set in the showChangePasswordScreen Method in the SceneManager
        User user = (User) ConfirmPassword.getScene().getUserData();
        if (Strings.isNullOrEmpty(ConfirmPassword.getText())) {
            eventBus.post(new ConfirmPasswordErrorEvent(resourceBundle.getString("changepw.error.empty.oldpw")));
        } else {
            userService.confirmUserPassword(user, ConfirmPassword.getText());
        }
    }

    /**
     * Method called when the button to cancel the process is pressed
     * <p>
     * This Method is called when the CancelButton is pressed. It posts an instance
     * of the ConfirmPasswordCanceledEvent onto the EventBus the SceneManager is subscribed
     * to.
     *
     * @author Eric Vuong
     * @author Alwin Bossert
     * @see de.uol.swp.client.ChangePassword.event.ChangePasswordCanceledEvent
     * @see de.uol.swp.client.SceneManager
     * @since 2021-03-16
     */
    public void onCancelButtonPressed() {
        eventBus.post(confirmPasswordCanceledEvent);
    }
}
