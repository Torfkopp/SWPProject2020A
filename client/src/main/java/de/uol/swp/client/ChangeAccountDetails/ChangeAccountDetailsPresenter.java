package de.uol.swp.client.ChangeAccountDetails;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.ChangeAccountDetails.event.ChangeAccountDetailsCanceledEvent;
import de.uol.swp.client.ChangeAccountDetails.event.ChangeAccountDetailsErrorEvent;
import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the ChangeAccountDetails window
 *
 * @author Eric Vuong
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2020-11-25
 */
@SuppressWarnings("UnstableApiUsage")
public class ChangeAccountDetailsPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/ChangeAccountDetailsView.fxml";
    private static final ChangeAccountDetailsCanceledEvent ChangeAccountDetailsCanceledEvent = new ChangeAccountDetailsCanceledEvent();
    private static final Logger LOG = LogManager.getLogger(MainMenuPresenter.class);
    @FXML
    private PasswordField NewPasswordField;
    @FXML
    private TextField NewUsernameField;
    @FXML
    private TextField NewEMailField;
    @FXML
    private PasswordField ConfirmPasswordField;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     * @author Eric Vuong
     * @author Steven Luong
     * @see de.uol.swp.client.di.ClientModule
     * @since 2020-11-25
     */
    @Inject
    public ChangeAccountDetailsPresenter(EventBus eventBus) {
        setEventBus(eventBus);
    }

    /**
     * Method called when the button to cancel the process is pressed
     * <p>
     * This Method is called when the CancelButton is pressed. It posts an instance
     * of the ChangeAccountDetailsCanceledEvent onto the EventBus the SceneManager is subscribed
     * to.
     *
     * @author Eric Vuong
     * @see de.uol.swp.client.ChangeAccountDetails.event.ChangeAccountDetailsCanceledEvent
     * @see de.uol.swp.client.SceneManager
     * @since 2020-11-25
     */
    @FXML
    private void onCancelButtonPressed() {
        eventBus.post(ChangeAccountDetailsCanceledEvent);
    }

    /**
     * Method called when the button to change the Account Detail is pressed
     * <p>
     * This Method is called when the ChangeAccountDetailsButton is pressed. It posts an instance
     * of the ChangeAccountDetailsErrorEvent onto the EventBus the SceneManager is subscribed
     * to, when at least one of the field is not empty or the ConfirmPassword is not correct.
     * If everything is filled in correctly, the user service is requested to update
     * the user's account detail.
     *
     * @author Eric Vuong
     * @author Steven Luong
     * @see de.uol.swp.client.ChangeAccountDetails.event.ChangeAccountDetailsErrorEvent
     * @see de.uol.swp.client.SceneManager
     * @see de.uol.swp.client.user.UserService
     * @since 2020-12-05
     */
    @FXML
    private void onChangeAccountDetailsButtonPressed() {
        //the UserData is set in the showChangePasswordScreen Method in the SceneManager

        if (Strings.isNullOrEmpty(ConfirmPasswordField.getText())) {
            eventBus.post(new ChangeAccountDetailsErrorEvent(
                    resourceBundle.getString("changeaccdetails.error.empty.changepw")));
        }

        User user = (User) ConfirmPasswordField.getScene().getUserData();
        String newPassword = ConfirmPasswordField.getText();
        String newUsername = user.getUsername();
        String newEMail = user.getEMail();

        if (Strings.isNullOrEmpty(NewUsernameField.getText()) && Strings
                .isNullOrEmpty(NewEMailField.getText()) && Strings.isNullOrEmpty(NewPasswordField.getText())) {
            eventBus.post(new ChangeAccountDetailsErrorEvent(
                    resourceBundle.getString("changeaccdetails.error.empty.changeaccdetails")));
        } else {
            if (!Strings.isNullOrEmpty(NewPasswordField.getText())) {
                newPassword = NewPasswordField.getText();
            }
            if (!Strings.isNullOrEmpty(NewUsernameField.getText())) {
                newUsername = NewUsernameField.getText();
            }
            if (!Strings.isNullOrEmpty(NewEMailField.getText())) {
                newEMail = NewEMailField.getText();
            }
            userService.updateAccountDetails(new UserDTO(user.getID(), newUsername, newPassword, newEMail),
                    ConfirmPasswordField.getText(), user.getUsername(), user.getEMail());
        }
    }
}
