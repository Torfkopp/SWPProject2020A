package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.chat.IChatService;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.common.chat.ChatMessage;
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
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Manages the lobby menu
 *
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2020-11-21
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/LobbyView.fxml";

    private static final Logger LOG = LogManager.getLogger(LobbyPresenter.class);

    private ObservableList<String> lobbyMembers;

    private User loggedInUser;
    private String lobbyName;
    private ObservableList<String> chatMessages;
    private ObservableMap<Integer, ChatMessage> chatMessageMap;

    @Inject
    private IChatService chatService;

    @FXML
    private ListView<String> chatView;

    @FXML
    private ListView<String> membersView;

    @FXML
    private TextField messageField;

    /**
     * Default Constructor
     *
     * @since 2020-11-21
     */
    public LobbyPresenter() {
    }

    @FXML
    protected void initialize() {
        prepareChatVars();
    }

    @Subscribe
    private void onLobbyUpdateEvent(LobbyUpdateEvent lobbyUpdateEvent) {
        if (lobbyName == null) {
            this.lobbyName = lobbyUpdateEvent.getLobbyName();
            this.loggedInUser = lobbyUpdateEvent.getUser();
            chatService.askLatestMessages(10, this.lobbyName);
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
            LOG.debug(rsp.getChatHistory());
            updateChatMessageList(rsp.getChatHistory());
        }
    }

    @Subscribe
    private void onCreatedChatMessageMessage(CreatedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(this.lobbyName)) {
            LOG.debug("Received Chat Message from " + msg.getMsg().getAuthor().getUsername()
                    + ": '" + msg.getMsg().getContent() + '\'');
            Platform.runLater(() -> chatMessageMap.put(msg.getMsg().getID(), msg.getMsg()));
        }
    }

    @Subscribe
    private void onDeletedChatMessageMessage(DeletedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(this.lobbyName)) {
            LOG.debug("Received instruction to delete ChatMessage with id " + msg.getId());
            Platform.runLater(() -> chatMessageMap.remove(msg.getId()));
        }
    }

    @Subscribe
    private void onEditedChatMessageMessage(EditedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(this.lobbyName)) {
            LOG.debug("Received instruction to edit ChatMessage with id " + msg.getMsg().getID() + " to: '"
                    + msg.getMsg().getContent() + '\'');
            Platform.runLater(() -> chatMessageMap.replace(msg.getMsg().getID(), msg.getMsg()));
        }
    }

    @FXML
    private void onSendMessageButtonPressed(ActionEvent event) {
        String msg = messageField.getText();
        messageField.clear();
        LOG.debug("Sending ChatMessage for lobby " + lobbyName + " ('" + msg + "') from " + this.loggedInUser.getUsername());
        chatService.newMessage(this.loggedInUser, msg, lobbyName);
    }

    @FXML
    private void onDeleteMessageButtonPressed(ActionEvent event) {
        Integer msgId = findId();
        if (msgId != null) {
            System.out.println("Calling chatService.deleteMessage(" + msgId + ", " + lobbyName + ");");
            chatService.deleteMessage(msgId, lobbyName);
        }
    }

    @FXML
    private void onEditMessageButtonPressed(ActionEvent event) {
        Integer msgId = findId();
        if (msgId != null) {
            System.out.println("Calling chatService.editMessage(" + msgId + ", " + messageField.getText() + ", " + lobbyName + ");");
            chatService.editMessage(msgId, messageField.getText(), lobbyName);
            messageField.clear();
        }
    }

    private void updateChatMessageList(List<ChatMessage> chatMessageList) {
        Platform.runLater(() -> {
            chatMessages.clear();
            chatMessageList.forEach(m -> chatMessageMap.put(m.getID(), m));
        });
    }

    private void prepareChatVars() {
        if (chatMessageMap == null) chatMessageMap = FXCollections.observableHashMap();
        if (chatMessages == null) chatMessages = FXCollections.observableArrayList();
        chatView.setItems(chatMessages);
        chatMessageMap.addListener((MapChangeListener<Integer, ChatMessage>) change -> {
            if (change.wasAdded() && !change.wasRemoved()) {
                chatMessages.add(change.getValueAdded().toString());
            } else if (!change.wasAdded() && change.wasRemoved()) {
                for (int i = 0; i < chatMessages.size(); i++) {
                    String text = chatMessages.get(i);
                    if (text.equals(change.getValueRemoved().toString())) {
                        chatMessages.remove(i);
                        break;
                    }
                }
                chatMessages.remove(change.getValueRemoved().toString());
            } else if (change.wasAdded() && change.wasRemoved()) {
                for (int i = 0; i < chatMessages.size(); i++) {
                    String text = chatMessages.get(i);
                    if (text.equals(change.getValueRemoved().toString())) {
                        chatMessages.remove(i);
                        chatMessages.add(i, change.getValueAdded().toString());
                        break;
                    }
                }
            }
        });
    }

    private Integer findId() {
        String msgText = chatView.getSelectionModel().getSelectedItem();
        Integer msgId = null;
        for (Map.Entry<Integer, ChatMessage> entry : chatMessageMap.entrySet()) {
            final ChatMessage selectedMessage = entry.getValue();
            if (selectedMessage.toString().equals(msgText) && selectedMessage.getAuthor().equals(this.loggedInUser)) {
                msgId = entry.getKey();
                break;
            }
        }
        return msgId;
    }
}
