package de.uol.swp.client.lobby;

import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.lobby.event.ConfirmLobbyPasswordEvent;
import de.uol.swp.common.lobby.request.JoinLobbyWithPasswordConfirmationRequest;
import de.uol.swp.common.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfirmLobbyPasswordPresenter extends AbstractPresenter {

    public static final String fxml1 = "/fxml/LobbyPasswordVerificationView.fxml";
    public static final int MIN_HEIGHT_PRE_GAME = 300;
    public static final int MIN_WIDTH_PRE_GAME = 200;
    private static final Logger LOG = LogManager.getLogger(ConfirmLobbyPasswordPresenter.class);
    private String lobbyName;
    @FXML
    private PasswordField passwordField;

    @FXML
    public void onConfirmLobbyPasswordButtonPressed() {
        if (Strings.isNullOrEmpty(passwordField.getText())) return;
        User user = userService.getLoggedInUser();
        eventBus.post(new JoinLobbyWithPasswordConfirmationRequest(lobbyName, user, passwordField.getText()));
    }

    @Subscribe
    private void onConfirmLobbyPasswordEvent(ConfirmLobbyPasswordEvent event) {
        LOG.debug("Received ConfirmLobbyPasswordEvent for Lobby " + event.getLobbyName());
        if (lobbyName == null) lobbyName = event.getLobbyName();
    }
}
