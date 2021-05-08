package de.uol.swp.client.changeAccountDetails;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.changeAccountDetails.event.ChangeAccountDetailsCanceledEvent;
import de.uol.swp.client.changeAccountDetails.event.ChangeAccountDetailsErrorEvent;
import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.function.UnaryOperator;
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
    public static final int MIN_HEIGHT = 390;
    public static final int MIN_WIDTH = 395;
    private static final ChangeAccountDetailsCanceledEvent changeAccountDetailsCanceledEvent = new ChangeAccountDetailsCanceledEvent();
    private static final Logger LOG = LogManager.getLogger(MainMenuPresenter.class);

    @FXML
    private Button changeButton;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private TextField newUsernameField;
    @FXML
    private TextField newEMailField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private PasswordField newPasswordField2;

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

    @FXML
    protected void initialize() {
        prepareNewUsernameField();
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
     * Helper method to manage the disableState of the "Change"-Button
     * <p>
     * This method returns a callable that will be used by the BooleanBinding
     * bound to the disableProperty of the "Change"-Button. The Callable
     * will check the contents of all fields and return true if the current
     * contents will not lead to a successful request.
     *
     * @return Callable that will return true if the button should be disabled,
     * false otherwise
     *
     * @implNote The user's password cannot be checked as the client will never
     * have access to any password information. The Callable will only check
     * that the user has typed in anything at all into the "Confirm Current
     * Password"-Field.
     * @author Sven Ahrens
     * @since 2021-04-22
     */
    private Callable<Boolean> manageChangeButtonState() {
        return () -> {
            boolean newNameEmpty = newUsernameField.getText().isEmpty();
            boolean newMailEmpty = newEMailField.getText().isEmpty();
            boolean newPWEmpty = newPasswordField.getText().isEmpty() && newPasswordField2.getText().isEmpty();
            boolean allEmpty = newNameEmpty && newMailEmpty && newPWEmpty;
            boolean newNameValid = newUsernameField.getText().matches("[A-Za-z0-9_-]+");
            boolean newMailValid = checkMailFormat(newEMailField.getText());
            boolean newPWValid = newPasswordField.getText().equals(newPasswordField2.getText());
            boolean oldPWEmpty = confirmPasswordField.getText().isEmpty();
            return oldPWEmpty || allEmpty || !newNameEmpty && !newNameValid || !newMailEmpty && !newMailValid || !newPWEmpty && !newPWValid;
        };
    }

    /**
     * Method called when the button to cancel the process is pressed
     * <p>
     * This Method is called when the CancelButton is pressed. It posts an instance
     * of the ChangeAccountDetailsCanceledEvent onto the EventBus the SceneManager is subscribed
     * to.
     *
     * @author Eric Vuong
     * @see de.uol.swp.client.changeAccountDetails.event.ChangeAccountDetailsCanceledEvent
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
     * @see de.uol.swp.client.changeAccountDetails.event.ChangeAccountDetailsErrorEvent
     * @see de.uol.swp.client.SceneManager
     * @see de.uol.swp.client.user.UserService
     * @since 2020-12-05
     */
    @FXML
    private void onChangeAccountDetailsButtonPressed() {
        if (Strings.isNullOrEmpty(confirmPasswordField.getText())) {
            eventBus.post(new ChangeAccountDetailsErrorEvent(
                    resourceBundle.getString("changeaccdetails.error.empty.changepw")));
        }

        User user = userService.getLoggedInUser();
        String newPassword = userService.hash(confirmPasswordField.getText());
        String newHashedPassword = userService.hash(newPasswordField.getText());
        String newConfirmHashedPassword = userService.hash(newPasswordField2.getText());
        String newUsername = user.getUsername();
        String newEMail = user.getEMail();

        if (Strings.isNullOrEmpty(newUsernameField.getText()) && Strings
                .isNullOrEmpty(newEMailField.getText()) && Strings.isNullOrEmpty(newPasswordField.getText()) && Strings
                    .isNullOrEmpty(newPasswordField2.getText())) {
            eventBus.post(new ChangeAccountDetailsErrorEvent(
                    resourceBundle.getString("changeaccdetails.error.empty.changeaccdetails")));
        } else if (!checkMailFormat(newEMailField.getText()) && !newEMailField.getText().isEmpty()) {
            eventBus.post(new ChangeAccountDetailsErrorEvent(resourceBundle.getString("register.error.invalid.email")));
        } else if (Strings.isNullOrEmpty(newPasswordField.getText()) && !Strings
                .isNullOrEmpty(newPasswordField2.getText())) {
            eventBus.post(
                    new ChangeAccountDetailsErrorEvent(resourceBundle.getString("changeaccdetails.error.empty.newpw")));
        } else if (!Strings.isNullOrEmpty(newPasswordField.getText()) && Strings
                .isNullOrEmpty(newPasswordField2.getText())) {
            eventBus.post(
                    new ChangeAccountDetailsErrorEvent(resourceBundle.getString("changeaccdetails.error.empty.newpw")));
        } else if (!newHashedPassword.equals(newConfirmHashedPassword)) {
            eventBus.post(new ChangeAccountDetailsErrorEvent(
                    resourceBundle.getString("changeaccdetails.error.empty.newpasswordconfirm")));
        } else {
            if (!Strings.isNullOrEmpty(newPasswordField.getText())) {
                newPassword = userService.hash(newPasswordField.getText());
            }
            if (!Strings.isNullOrEmpty(newUsernameField.getText())) {
                newUsername = newUsernameField.getText();
            }
            if (!Strings.isNullOrEmpty(newEMailField.getText())) {
                newEMail = newEMailField.getText();
            }
            userService.updateAccountDetails(new UserDTO(user.getID(), newUsername, newPassword, newEMail),
                                             userService.hash(confirmPasswordField.getText()), user.getUsername(),
                                             user.getEMail());
        }
    }

    /**
     * Prepares the newUsernameField
     * <p>
     * Helper method, called when the ChangeAccountDetailsPresenter is initialised in order to let the newUsernameField
     * only accept alphanumeric entries with the addition of underscore and hyphen
     *
     * @author Sven Ahrens
     * @since 2021-04-22
     */
    private void prepareNewUsernameField() {
        UnaryOperator<TextFormatter.Change> StringFilter = (s) ->
                s.getText().matches("[A-Za-z0-9_-]+") || s.isDeleted() || s.getText().equals("") ? s : null;
        newUsernameField.setTextFormatter(new TextFormatter<>(StringFilter));

        changeButton.disableProperty()
                    .bind(Bindings.createBooleanBinding(manageChangeButtonState(), newPasswordField.textProperty(),
                                                        newPasswordField2.textProperty(),
                                                        confirmPasswordField.textProperty(),
                                                        newUsernameField.textProperty(), newEMailField.textProperty()));
    }
}