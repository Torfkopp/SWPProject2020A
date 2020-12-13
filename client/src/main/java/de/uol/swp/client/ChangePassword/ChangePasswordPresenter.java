package de.uol.swp.client.ChangePassword;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.ChangePassword.event.ChangePasswordCanceledEvent;
import de.uol.swp.client.ChangePassword.event.ChangePasswordErrorEvent;
import de.uol.swp.client.user.ClientUserService;
import de.uol.swp.common.user.UserDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

/**
 * Manages the change password window
 *
 * @author Eric Vuong
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2020-11-25
 *
 */

public class ChangePasswordPresenter extends AbstractPresenter {
    public static final String fxml = "/fxml/ChangePasswordView.fxml";

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
     * @param eventBus The EventBus set in ClientModule
     * @param userService The injected ClientUserService
     * @see de.uol.swp.client.di.ClientModule
     * @author Eric Vuong, Steven Luong
     * @since 2020-11-25
     *
     */
    @Inject
    public ChangePasswordPresenter(EventBus eventBus, ClientUserService userService) {
        setEventBus(eventBus);
    }

    /**
     * Method called when the cancel button is pressed
     *
     * This Method is called when the cancel button is pressed. It posts an instance
     * of the ChangePasswordCanceledEvent to the EventBus the SceneManager is subscribed
     * to.
     *
     * @param event The ActionEvent generated by pressing the ChangePassword button
     * @see de.uol.swp.client.ChangePassword.event.ChangePasswordCanceledEvent
     * @see de.uol.swp.client.SceneManager
     * @author Eric Vuong
     * @since 2020-11-25
     *
     */
    @FXML
    void onCancelButtonPressed(ActionEvent event) {
        eventBus.post(ChangePasswordCanceledEvent);
    }

    /**
     * Method called when the changePassword button is pressed
     *
     * This Method is called when the ChangePassword button is pressed. It posts an instance
     * of the ChangePasswordErrorEvent to the EventBus the SceneManager is subscribed
     * to, if one of the fields is empty or the password fields are not equal.
     * If everything is filled in correctly the user service is requested to update
     * a user password.
     *
     * @param event The ActionEvent generated by pressing the ChangePassword button
     * @see de.uol.swp.client.ChangePassword.event.ChangePasswordErrorEvent
     * @see de.uol.swp.client.SceneManager
     * @see de.uol.swp.client.user.UserService
     * @since 2020-12-05
     *
     */
    @FXML
    void onChangePasswordButtonPressed(ActionEvent event) {
        if (Strings.isNullOrEmpty(OldPasswordField1.getText())){
            eventBus.post(new ChangePasswordErrorEvent("Old Password cannot be empty"));
        }  else if (Strings.isNullOrEmpty(NewPasswordField1.getText())) {
            eventBus.post(new ChangePasswordErrorEvent("New Password cannot be empty"));
        } else if (Strings.isNullOrEmpty(NewPasswordField2.getText())) {
            eventBus.post(new ChangePasswordErrorEvent("Repeat new Password cannot be empty"));
        } else if (!NewPasswordField1.getText().equals(NewPasswordField2.getText())) {
            eventBus.post(new ChangePasswordErrorEvent("Your New Passwords are not equal"));
        } else {
            userService.updateUser(new UserDTO());
        }
    }

}


