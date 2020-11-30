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
     */
    @FXML
    void onCancelButtonPressed(ActionEvent event) {
        eventBus.post(ChangePasswordCanceledEvent);
    }
}
