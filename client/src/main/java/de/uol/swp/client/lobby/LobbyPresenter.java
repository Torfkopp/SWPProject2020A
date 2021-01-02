package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenterWithChat;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.common.chat.message.CreatedChatMessageMessage;
import de.uol.swp.common.chat.message.DeletedChatMessageMessage;
import de.uol.swp.common.chat.message.EditedChatMessageMessage;
import de.uol.swp.common.chat.response.AskLatestChatMessageResponse;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.response.AllLobbyMembersResponse;
import de.uol.swp.common.user.UserDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;

import java.util.List;

/**
 * Manages the lobby menu
 *
 * @see de.uol.swp.client.AbstractPresenterWithChat
 * @since 2020-11-21
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyPresenter extends AbstractPresenterWithChat {

    public static final String fxml = "/fxml/LobbyView.fxml";

    private ObservableList<String> lobbyMembers;

    @FXML
    private ListView<String> membersView;


    /**
     * Default Constructor
     *
     * @since 2020-11-21
     */
    public LobbyPresenter() {
        super.init(LogManager.getLogger(LobbyPresenter.class));
    }

    @Subscribe
    private void onLobbyUpdateEvent(LobbyUpdateEvent lobbyUpdateEvent) {
        if (super.lobbyName == null) {
            super.lobbyName = lobbyUpdateEvent.getLobbyName();
            super.loggedInUser = lobbyUpdateEvent.getUser();
            super.chatService.askLatestMessages(10, super.lobbyName);
        } else System.out.println("not for me?");
    }

    /**
     * Handles new list of users
     * <p>
     * If a new AllOnlineUsersResponse object is posted to the EventBus the names
     * of currently logged in members are put onto the list of lobby members.
     * Furthermore if the LOG-Level is set to DEBUG the message "Update of user
     * list" with the names of all currently logged in users is displayed in the
     * log.
     *
     * @param allLobbyMembersResponse AllLobbyMembersResponse object seen on the EventBus
     * @see de.uol.swp.common.lobby.response.AllLobbyMembersResponse
     * @since 2020-11-22
     */
    @Subscribe
    public void onAllLobbyMembersResponse(AllLobbyMembersResponse allLobbyMembersResponse) {
        LOG.debug("Update of lobby member list " + allLobbyMembersResponse.getUsers());
        updateUsersList(allLobbyMembersResponse.getUsers());
    }

    /**
     * Handles new joined users
     * <p>
     * If a new UserJoinedLobbyMessage object is posted to the EventBus the name of the newly
     * joined user is appended to the user list in the lobby menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "New user {@literal
     * <Username>} joined Lobby." is displayed in the log.
     *
     * @param message the UserJoinedLobbyMessage object seen on the EventBus
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2020-11-22
     */
    @Subscribe
    public void onUserJoinedLobbyMessage(UserJoinedLobbyMessage message) {
        LOG.debug("New user " + message.getUser().getUsername() + " joined Lobby " + message.getName());
        Platform.runLater(() -> {
            if (lobbyMembers != null && loggedInUser != null && !loggedInUser.getUsername().equals(message.getUser().getUsername()))
                lobbyMembers.add(message.getUser().getUsername());
        });
    }

    /**
     * Updates the lobby's member list according to the list given
     * <p>
     * This method clears the entire member list and then adds the name of each user
     * in the list given to the lobby's member list. If there is no member list
     * this it creates one.
     *
     * @param userLobbyList A list of UserDTO objects including all currently logged in
     *                      users
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore it is crucial not to remove the {@code Platform.runLater()}
     * @see de.uol.swp.common.user.UserDTO
     * @since 2020-11-22
     */
    private void updateUsersList(List<UserDTO> userLobbyList) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (lobbyMembers == null) {
                lobbyMembers = FXCollections.observableArrayList();
                membersView.setItems(lobbyMembers);
            }
            lobbyMembers.clear();
            userLobbyList.forEach(u -> lobbyMembers.add(u.getUsername()));
        });
    }

    @Subscribe
    private void onAskLatestChatMessagesResponse(AskLatestChatMessageResponse rsp) {
        if (rsp.getLobbyName().equals(this.lobbyName)) {
            super.onAskLatestChatMessageResponse(rsp);
        }
    }

    @Subscribe
    protected void onCreatedChatMessageMessage(CreatedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(this.lobbyName)) {
            super.onCreatedChatMessageMessage(msg);
        }
    }


    @Subscribe
    protected void onDeletedChatMessageMessage(DeletedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(this.lobbyName)) {
            super.onDeletedChatMessageMessage(msg);
        }
    }

    @Subscribe
    protected void onEditedChatMessageMessage(EditedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(this.lobbyName)) {
            super.onEditedChatMessageMessage(msg);
        }
    }
}
