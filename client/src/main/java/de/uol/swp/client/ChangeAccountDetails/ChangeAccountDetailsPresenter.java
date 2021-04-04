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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final ChangeAccountDetailsCanceledEvent changeAccountDetailsCanceledEvent = new ChangeAccountDetailsCanceledEvent();
    private static final Logger LOG = LogManager.getLogger(MainMenuPresenter.class);
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private TextField newUsernameField;
    @FXML
    private TextField newEMailField;
    @FXML
    private PasswordField confirmPasswordField;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     *
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
        eventBus.post(changeAccountDetailsCanceledEvent);
    }

    /**
     * Method called when the button to change the Account Detail is pressed
     * <p>
     * This Method is called when the ChangeAccountDetailsButton is pressed. It posts an instance
     * of the ChangeAccountDetailsErrorEvent onto the EventBus the SceneManager is subscribed
     * to, when at least one of the field is not empty or the ConfirmPassword is not correct.
     * If the EMail-format is correct and everything is filled in correctly, the user service is requested to update
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

        if (Strings.isNullOrEmpty(confirmPasswordField.getText())) {
            eventBus.post(new ChangeAccountDetailsErrorEvent(
                    resourceBundle.getString("changeaccdetails.error.empty.changepw")));
        }

        User user = userService.getLoggedInUser();
        String newPassword = confirmPasswordField.getText();
        String newUsername = user.getUsername();
        String newEMail = user.getEMail();

        if (Strings.isNullOrEmpty(newUsernameField.getText()) && Strings
                .isNullOrEmpty(newEMailField.getText()) && Strings.isNullOrEmpty(newPasswordField.getText())) {
            eventBus.post(new ChangeAccountDetailsErrorEvent(
                    resourceBundle.getString("changeaccdetails.error.empty.changeaccdetails")));
        } else if (!checkMailFormat(newEMailField.getText()) && !newEMailField.getText().isEmpty()) {
            eventBus.post(new ChangeAccountDetailsErrorEvent(resourceBundle.getString("register.error.invalid.email")));
        } else {
            if (!Strings.isNullOrEmpty(newPasswordField.getText())) {
                newPassword = newPasswordField.getText();
            }
            if (!Strings.isNullOrEmpty(newUsernameField.getText())) {
                newUsername = newUsernameField.getText();
            }
            if (!Strings.isNullOrEmpty(newEMailField.getText())) {
                newEMail = newEMailField.getText();
            }
            userService.updateAccountDetails(new UserDTO(user.getID(), newUsername, newPassword, newEMail),
                                             confirmPasswordField.getText(), user.getUsername(), user.getEMail());
        }
    }
}
