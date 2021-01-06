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
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;

import java.util.List;

/**
 * Manages the lobby's menu
 *
 * @see de.uol.swp.client.AbstractPresenter
 * @see de.uol.swp.client.AbstractPresenterWithChat
 * @since 2020-11-21
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyPresenter extends AbstractPresenterWithChat {

    public static final String fxml = "/fxml/LobbyView.fxml";

    private ObservableList<String> lobbyMembers;
    private User owner;

    @FXML
    private ListView<String> membersView;

    /**
     * Constructor
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.AbstractPresenterWithChat
     * @since 2021-01-02
     */
    public LobbyPresenter() {
        super.init(LogManager.getLogger(LobbyPresenter.class));
    }

    /**
     * Handles LobbyUpdateEvents on the EventBus
     * <p>
     * If a new LobbyUpdateEvent is posted to the EventBus, this method checks
     * whether the lobbyName and/or loggedInUser attributes of the current
     * LobbyPresenter are null. If they are, it sets these attributes to the
     * values found in the LobbyUpdateEvent.
     *
     * @param lobbyUpdateEvent the lobby update event
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.lobby.event.LobbyUpdateEvent
     * @since 2020-12-30
     */
    @Subscribe
    private void onLobbyUpdateEvent(LobbyUpdateEvent lobbyUpdateEvent) {
        if (super.lobbyName == null || loggedInUser == null) {
            super.lobbyName = lobbyUpdateEvent.getLobbyName();
            super.loggedInUser = lobbyUpdateEvent.getUser();
            super.chatService.askLatestMessages(10, super.lobbyName);
        }
    }

    /**
     * Handles a new list of users
     * <p>
     * If a new AllOnlineUsersResponse object is posted onto the EventBus, the names
     * of the currently logged in members are put into the list of lobby members.
     * Furthermore, if the LOG-Level is set to DEBUG, the message "Update of user
     * list" with the names of all currently logged in users as well as the message
     * "Owner of this lobby: " with the name of the lobby's owner is displayed in the
     * log.
     *
     * @param allLobbyMembersResponse AllLobbyMembersResponse object seen on the EventBus
     * @see de.uol.swp.common.lobby.response.AllLobbyMembersResponse
     * @since 2021-01-05
     */
    @Subscribe
    private void onAllLobbyMembersResponse(AllLobbyMembersResponse allLobbyMembersResponse) {
        LOG.debug("Update of lobby member list " + allLobbyMembersResponse.getUsers());
        LOG.debug("Owner of this lobby: " + allLobbyMembersResponse.getOwner().getUsername());
        this.owner = allLobbyMembersResponse.getOwner();
        updateUsersList(allLobbyMembersResponse.getUsers());
    }

    /**
     * Handles new joined users
     * <p>
     * If a new UserJoinedLobbyMessage object is posted onto the EventBus, the name of the newly
     * joined user is appended to the user list in the lobby menu.
     * Furthermore, if the LOG-Level is set to DEBUG, the message "New user {@literal
     * <Username>} joined Lobby." is displayed in the log.
     *
     * @param message the UserJoinedLobbyMessage object seen on the EventBus
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2020-11-22
     */
    @Subscribe
    private void onUserJoinedLobbyMessage(UserJoinedLobbyMessage message) {
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
     * in the list given to the lobby's member list.
     * If there is no member list, it creates one.
     * If the owner is found among the users, their username is appended with a
     * crown emoji.
     *
     * @param userLobbyList A list of UserDTO objects including all currently logged in
     *                      users
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore it is crucial not to remove the {@code Platform.runLater()}
     * @see de.uol.swp.common.user.UserDTO
     * @since 2021-01-05
     */
    private void updateUsersList(List<UserDTO> userLobbyList) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (lobbyMembers == null) {
                lobbyMembers = FXCollections.observableArrayList();
                membersView.setItems(lobbyMembers);
            }
            lobbyMembers.clear();
            userLobbyList.forEach(u -> {
                String username = u.getUsername();
                lobbyMembers.add(username.equals(this.owner.getUsername()) ? username + "\uD83D\uDC51" : username);
            });
        });
    }

    @Override
    @Subscribe
    protected void onCreatedChatMessageMessage(CreatedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(super.lobbyName)) {
            super.onCreatedChatMessageMessage(msg);
        }
    }

    @Override
    @Subscribe
    protected void onDeletedChatMessageMessage(DeletedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(super.lobbyName)) {
            super.onDeletedChatMessageMessage(msg);
        }
    }

    @Override
    @Subscribe
    protected void onEditedChatMessageMessage(EditedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(super.lobbyName)) {
            super.onEditedChatMessageMessage(msg);
        }
    }

    @Override
    @Subscribe
    protected void onAskLatestChatMessageResponse(AskLatestChatMessageResponse rsp) {
        if (rsp.getLobbyName().equals(super.lobbyName)) {
            super.onAskLatestChatMessageResponse(rsp);
        }
    }
}
