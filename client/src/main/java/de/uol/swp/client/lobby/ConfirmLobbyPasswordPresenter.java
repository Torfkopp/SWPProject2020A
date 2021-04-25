package de.uol.swp.client.lobby;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.lobby.event.ConfirmLobbyPasswordCanceledEvent;
import de.uol.swp.client.lobby.event.ConfirmLobbyPasswordEvent;
import de.uol.swp.common.lobby.request.JoinLobbyWithPasswordConfirmationRequest;
import de.uol.swp.common.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the ConfirmLobbyPassword window
 *
 * @author Alwin Bossert
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-04-22
 */
public class ConfirmLobbyPasswordPresenter extends AbstractPresenter {

    public static final String fxml1 = "/fxml/LobbyPasswordVerificationView.fxml";
    public static final int MIN_HEIGHT_PRE_GAME = 300;
    public static final int MIN_WIDTH_PRE_GAME = 400;
    private static final Logger LOG = LogManager.getLogger(ConfirmLobbyPasswordPresenter.class);
    private String lobbyName;
    @FXML
    private PasswordField passwordField;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     *
     * @author Alwin Bossert
     * @since 2021-04-22
     */
    @Inject
    public ConfirmLobbyPasswordPresenter(EventBus eventBus) {
        setEventBus(eventBus);
    }

    /**
     * Method called when the button to confirm the process is pressed
     * <p>
     * This Method is called when the ConfirmLobbyPasswordButton is pressed. It posts an instance
     * of the JoinLobbyWithPasswordConfirmationRequest onto the EventBus the SceneManager is subscribed
     * to.
     *
     * @author Alwin Bossert
     * @see de.uol.swp.client.SceneManager
     * @since 2021-04-22
     */
    @FXML
    public void onConfirmLobbyPasswordButtonPressed() {
        if (Strings.isNullOrEmpty(passwordField.getText())) return;
        User user = userService.getLoggedInUser();
        eventBus.post(new JoinLobbyWithPasswordConfirmationRequest(lobbyName, user, passwordField.getText()));
    }

    @FXML
    public void onCancelButtonPressed(){
        eventBus.post(new ConfirmLobbyPasswordCanceledEvent());
    }

    /**
     * Handles a ConfirmLobbyPasswordEvent
     * <p>
     * If the lobbyname or the logged in user of the ConfirmLobbyPasswordPresenter are
     * null, they get the parameters of the event. This Event is sent when a new
     * ConfirmLobbyPasswordPresenter is created. If a window is closed using e.g.
     * X(top-right-Button), the closeWindow method is called.
     *
     * @param event ConfirmLobbyPasswordEvent found on the event bus
     *
     * @see de.uol.swp.client.lobby.event.ConfirmLobbyPasswordEvent
     */
    @Subscribe
    private void onConfirmLobbyPasswordEvent(ConfirmLobbyPasswordEvent event) {
        LOG.debug("Received ConfirmLobbyPasswordEvent for Lobby " + event.getLobbyName());
        if (lobbyName == null) lobbyName = event.getLobbyName();
    }
}
