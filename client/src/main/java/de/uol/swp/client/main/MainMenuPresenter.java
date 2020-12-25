package de.uol.swp.client.main;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.lobby.event.LobbyErrorEvent;
import de.uol.swp.client.lobby.event.ShowLobbyViewEvent;
import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.chat.message.CreatedChatMessageMessage;
import de.uol.swp.common.chat.message.DeletedChatMessageMessage;
import de.uol.swp.common.chat.message.EditedChatMessageMessage;
import de.uol.swp.common.chat.response.AskLatestChatMessageResponse;
import de.uol.swp.common.lobby.response.AllLobbiesResponse;
import de.uol.swp.common.lobby.message.LobbyCreatedMessage;
import de.uol.swp.common.lobby.message.LobbyDeletedMessage;
import de.uol.swp.common.lobby.response.CreateLobbyResponse;
import de.uol.swp.common.lobby.response.JoinLobbyResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import de.uol.swp.client.ChangePassword.event.ShowChangePasswordViewEvent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Manages the main menu
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2019-08-29
 */
@SuppressWarnings("UnstableApiUsage")
public class MainMenuPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/MainMenuView.fxml";

    private static final Logger LOG = LogManager.getLogger(MainMenuPresenter.class);

    private static final ShowLoginViewEvent showLoginViewMessage = new ShowLoginViewEvent();

    private ObservableList<String> users;
    private ObservableMap<Integer, ChatMessage> chatMessageMap;
    private ObservableList<String> chatMessages;

    private ObservableList<String> lobbies;

    private User loggedInUser;

    @Inject
    private LobbyService lobbyService;

    @Inject
    private ChatService chatService;

    @FXML
    private ListView<String> chatView;

    @FXML
    private ListView<String> usersView;

    @FXML
    private TextField messageField;

    @FXML
    private ListView<String> lobbyView;

    /**
     * Handles successful login
     * <p>
     * If a LoginSuccessfulResponse is posted to the EventBus the loggedInUser
     * of this client is set to the one in the message received and the full
     * list of users currently logged in is requested, as well as the list
     * of lobbies.
     *
     * @param message the LoginSuccessfulResponse object seen on the EventBus
     * @see de.uol.swp.common.user.response.LoginSuccessfulResponse
     * @since 2019-09-05
     */
    @Subscribe
    public void loginSuccessful(LoginSuccessfulResponse message) {
        prepareChatVars();
        this.loggedInUser = message.getUser();
        userService.retrieveAllUsers();
        lobbyService.retrieveAllLobbies();
        chatService.askLatestMessages(10);
    }

    /**
     * Prepares the variables used for the chat storage and management
     * <p>
     * This method is called on a successful login and ensures that
     * the used variables chatMessageMap and chatMessages aren't null,
     * sets the items of the chatView to the chatMessages observableList,
     * and adds a MapChangeListener that manages the displayed ChatMessages.
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @since 2020-12-20
     */
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

    /**
     * Handles lobbyCreatedMessage
     * <p>
     * If a Lobby gets created, this method is called.
     * It updates the LobbyList.
     *
     * @param lobbyCreatedMessage the LobbyCreatedMessage object seen on the EventBus
     * @see de.uol.swp.common.lobby.message.LobbyCreatedMessage
     * @since 2020-12-14
     */
    @Subscribe
    public void newLobby(LobbyCreatedMessage lobbyCreatedMessage) {
        lobbyService.retrieveAllLobbies();
    }

    /**
     * Handles new logged in users
     * <p>
     * If a new UserLoggedInMessage object is posted to the EventBus the name of the newly
     * logged in user is appended to the user list in the main menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "New user {@literal
     * <Username>} logged in." is displayed in the log.
     *
     * @param message the UserLoggedInMessage object seen on the EventBus
     * @see de.uol.swp.common.user.message.UserLoggedInMessage
     * @since 2019-08-29
     */
    @Subscribe
    public void newUser(UserLoggedInMessage message) {

        LOG.debug("New user " + message.getUsername() + " logged in");
        Platform.runLater(() -> {
            if (users != null && loggedInUser != null && !loggedInUser.getUsername().equals(message.getUsername()))
                users.add(message.getUsername());
        });
    }

    /**
     * Handles new logged out users
     * <p>
     * If a new UserLoggedOutMessage object is posted to the EventBus the name of the newly
     * logged out user is removed from the user list in the main menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "User {@literal
     * <Username>} logged out." is displayed in the log.
     *
     * @param message the UserLoggedOutMessage object seen on the EventBus
     * @see de.uol.swp.common.user.message.UserLoggedOutMessage
     * @since 2019-08-29
     */
    @Subscribe
    public void userLeft(UserLoggedOutMessage message) {
        LOG.debug("User " + message.getUsername() + " logged out");
        Platform.runLater(() -> users.remove(message.getUsername()));
    }

    /**
     * Handles new list of users
     * <p>
     * If a new AllOnlineUsersResponse object is posted to the EventBus the names
     * of currently logged in users are put onto the user list in the main menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "Update of user
     * list" with the names of all currently logged in users is displayed in the
     * log.
     *
     * @param allUsersResponse the AllOnlineUsersResponse object seen on the EventBus
     * @see de.uol.swp.common.user.response.AllOnlineUsersResponse
     * @since 2019-08-29
     */
    @Subscribe
    public void userList(AllOnlineUsersResponse allUsersResponse) {
        LOG.debug("Update of user list " + allUsersResponse.getUsers());
        updateUsersList(allUsersResponse.getUsers());
    }

    /**
     * Handles new incoming ChatMessage
     * <p>
     * If a CreatedChatMessageMessage is posted to the EventBus, this method
     * puts the incoming ChatMessage's content into the chatMessageMap with the
     * ChatMessage's ID as the key.
     * If the loglevel is set to DEBUG, the message "Received Chat Message: " with
     * the incoming ChatMessage's content is displayed in the log.
     *
     * @param msg The CreatedChatMessageMessage object found on the EventBus
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see CreatedChatMessageMessage
     * @see MainMenuPresenter#chatMessageMap
     * @since 2020-12-17
     */
    @Subscribe
    public void onCreatedChatMessageMessage(CreatedChatMessageMessage msg) {
        LOG.debug("Received Chat Message from " + msg.getMsg().getAuthor().getUsername()
                + ": '" + msg.getMsg().getContent() + '\'');
        Platform.runLater(() -> chatMessageMap.put(msg.getMsg().getID(), msg.getMsg()));
    }

    /**
     * Handles incoming notification that a ChatMessage was deleted
     * <p>
     * If a DeletedChatMessageMessage is posted to the EventBus, this method
     * removes the ChatMessage with the corresponding ID from the chatMessageMap.
     *
     * @param msg The DeletedChatMessageMessage found on the EventBus
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see DeletedChatMessageMessage
     * @see MainMenuPresenter#chatMessageMap
     * @since 2020-12-17
     */
    @Subscribe
    public void onDeletedChatMessageMessage(DeletedChatMessageMessage msg) {
        Platform.runLater(() -> chatMessageMap.remove(msg.getId()));
    }

    /**
     * Handles incoming notification that a ChatMessage was edited
     * <p>
     * If an EditedChatMessageMessage is posted to the EventBus, this method
     * replaces the content in the chatMessageMap that is stored under the
     * edited ChatMessage's ID.
     *
     * @param msg The EditedChatMessageMessage found on the EventBus
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see EditedChatMessageMessage
     * @see MainMenuPresenter#chatMessageMap
     * @since 2020-12-17
     */
    @Subscribe
    public void onEditedChatMessageMessage(EditedChatMessageMessage msg) {
        Platform.runLater(() -> chatMessageMap.replace(msg.getMsg().getID(), msg.getMsg()));
    }

    /**
     * Handles AskLatestChatMessageResponse
     * <p>
     * If a AskLatestChatMessageResponse is found on the EventBus,
     * this method calls updateChatMessageList to fill or update the ChatMessageList.
     *
     * @param msg The AskLatestChatMessageResponse found on the EventBus
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see AskLatestChatMessageResponse
     * @see MainMenuPresenter#updateChatMessageList(List)
     * @since 2020-12-17
     */
    @Subscribe
    public void onAskLatestChatMessageResponse(AskLatestChatMessageResponse msg) {
        LOG.debug(msg.getChatHistory());
        updateChatMessageList(msg.getChatHistory());
    }

    /**
     * Method to update the ChatMessageList with a given List of ChatMessages
     *
     * @param chatMessageList The List of ChatMessages to insert into the
     *                        chatMessageMap
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see MainMenuPresenter#chatMessageMap
     * @since 2020-12-17
     */
    private void updateChatMessageList(List<ChatMessage> chatMessageList) {
        Platform.runLater(() -> {
            chatMessages.clear();
            chatMessageList.forEach(m -> chatMessageMap.put(m.getID(), m));
        });
    }

    /**
     * Updates the main menus user list according to the list given
     * <p>
     * This method clears the entire user list and then adds the name of each user
     * in the list given to the main menus user list. If there ist no user list
     * this it creates one.
     *
     * @param userList A list of User objects including all currently logged in
     *                 users
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore it is crucial not to remove the {@code Platform.runLater()}
     * @see de.uol.swp.common.user.User
     * @since 2019-08-29
     */
    private void updateUsersList(List<User> userList) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (users == null) {
                users = FXCollections.observableArrayList();
                usersView.setItems(users);
            }
            users.clear();
            userList.forEach(u -> users.add(u.getUsername()));
        });
    }

    /**
     * Adds newly created Lobby to LobbyList
     * <p>
     * If a new LobbyCreatedMessage object is posted to the EventBus the name
     * of the newly created lobby is put onto the lobby list in the main menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "Added new lobby to lobby
     * list" with the name of the newly added lobby is displayed in the
     * log.
     *
     * @param msg the LobbyCreatedMessage object seen on the EventBus
     * @author Temmo Junkhoff
     * @see LobbyCreatedMessage
     * @since 2020-12-17
     */
    @Subscribe
    private void onLobbyCreatedMessage(LobbyCreatedMessage msg) {

        if (msg.getName() == null || msg.getName().isEmpty()) {
            LOG.debug("Tried to add Lobby without name to LobbyList ");
        } else {
            Platform.runLater(() -> lobbies.add(msg.getName()));
            LOG.debug("Added Lobby to LobbyList " + msg.getName());
        }
    }

    /**
     * Handles CreateLobbyResponses found on the EventBus
     * <p>
     * If a new CreateLobbyResponse object is found on the EventBus, this method
     * posts a new ShowLobbyViewEvent to the EventBus the SceneManager is
     * subscribed to, and then calls the LobbyService to retrieve
     * all members of that new lobby in order for the lobby window to be
     * able to display all members from the beginning.
     *
     * @param createLobbyResponse The CreateLobbyResponse object found on the EventBus
     * @see CreateLobbyResponse
     * @see ShowLobbyViewEvent
     * @see LobbyService#retrieveAllLobbyMembers(String)
     * @since 2020-12-20
     */
    @Subscribe
    public void onCreateLobbyResponse(CreateLobbyResponse createLobbyResponse) {
        Platform.runLater(() -> {
            eventBus.post(new ShowLobbyViewEvent(createLobbyResponse.getName()));
            lobbyService.retrieveAllLobbyMembers(createLobbyResponse.getName());
        });
    }

    /**
     * Removes deleted Lobby from LobbyList
     * <p>
     * If a new LobbyDeletedMessage object is posted to the EventBus the name
     * of the deleted lobby is removed from the lobby list in the main menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "Removed lobby from lobby
     * list" with the name of the removed lobby is displayed in the
     * log.
     *
     * @param msg the LobbyDeletedMessage object seen on the EventBus
     * @author Temmo Junkhoff
     * @see LobbyDeletedMessage
     * @since 2020-12-17
     */
    @Subscribe
    private void onLobbyDeletedMessage(LobbyDeletedMessage msg) {

        if (msg.getName() == null || msg.getName().isEmpty()) {
            LOG.debug("Tried to delete Lobby without name from LobbyList ");
        } else {
            lobbies.remove(msg.getName());
            LOG.debug("Removed Lobby from LobbyList " + msg.getName());
        }
    }


    /**
     * Handles new list of lobbies
     * <p>
     * If a new AllLobbiesResponse object is posted to the EventBus the names
     * of currently existing lobbies are put onto the lobby list in the main menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "Update of lobby
     * list" with the names of all currently existing lobbies is displayed in the
     * log.
     *
     * @param allLobbiesResponse the AllLobbiesResponse object seen on the EventBus
     * @see de.uol.swp.common.lobby.response.AllLobbiesResponse
     * @since 2020-11-29
     */
    @Subscribe
    public void lobbyList(AllLobbiesResponse allLobbiesResponse) {
        updateLobbyList(allLobbiesResponse.getLobbyNames());
    }

    /**
     * Updates the main menus lobby list according to the list given
     * <p>
     * This method clears the entire lobby list and then adds the name of each lobby
     * in the list given to the main menus lobby list. If there is no lobby list
     * this creates one.
     *
     * @param lobbyList A list of LobbyDTO objects including all currently existing
     *                  lobbies
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore it is crucial not to remove the {@code Platform.runLater()}
     * @see de.uol.swp.common.lobby.dto.LobbyDTO
     * @since 2020-11-29
     */

    private void updateLobbyList(List<String> lobbyList) {
        LOG.debug("Update Lobby List");
        Platform.runLater(() -> {
            if (lobbies == null) {
                lobbies = FXCollections.observableArrayList();
                lobbyView.setItems(lobbies);
            }
            lobbies.clear();
            lobbies.addAll(lobbyList);
        });
    }

    /**
     * Method called when the create lobby button is pressed
     * <p>
     * If the create lobby button is pressed, this method requests the lobby service
     * to create a new lobby. This lobby will get a unique name and registers the user as its creator.
     *
     * @param event The ActionEvent created by pressing the create lobby button
     * @author Mario and Marvin
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2020-12-11
     */
    @FXML
    void onCreateLobby(ActionEvent event) {
        //give the lobby a default name
        String name = loggedInUser.getUsername() + "'s lobby";

        //create Dialogue
        TextInputDialog dialog = new TextInputDialog(name);
        dialog.setTitle("Lobby Name");
        dialog.setHeaderText("Choose how to name your lobby");
        dialog.setContentText("Please enter the lobby's name: ");

        //if 'OK' is pressed the lobby will be created, otherwise it won't
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> lobbyService.createNewLobby(s, loggedInUser));
    }

    /**
     * Method called when the join lobby button is pressed
     * <p>
     * If the join lobby button is pressed, this method requests the lobby service
     * to join a specified lobby. If there is no existing lobby or the user didnt choose one,
     * nothing will happen.
     *
     * @param event The ActionEvent created by pressing the join lobby button
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2020-11-29
     */
    @FXML
    void onJoinLobby(ActionEvent event) {
        String lobbyName;

        lobbyView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        if (lobbyView.getSelectionModel().isEmpty()) {
            eventBus.post(new LobbyErrorEvent("Please choose a valid Lobby"));
        } else {
            lobbyName = lobbyView.getSelectionModel().getSelectedItem();
            lobbyService.joinLobby(lobbyName, loggedInUser);
        }

    }

    /**
     * Handles JoinLobbyResponses found on the EventBus
     * <p>
     * If a new JoinLobbyResponse object is found on the EventBus, this method
     * posts a new ShowLobbyViewEvent to the EventBus the SceneManager is
     * subscribed to, and then calls the LobbyService to retrieve
     * all members of that new lobby in order for the lobby window to be
     * able to display all members from the beginning.
     *
     * @param joinLobbyResponse The JoinLobbyResponse object found on the EventBus
     * @see JoinLobbyResponse
     * @see ShowLobbyViewEvent
     * @see LobbyService#retrieveAllLobbyMembers(String)
     * @since 2020-12-20
     */
    @Subscribe
    public void onJoinLobbyResponse(JoinLobbyResponse joinLobbyResponse) {
        Platform.runLater(() -> {
            eventBus.post(new ShowLobbyViewEvent(joinLobbyResponse.getName()));
            lobbyService.retrieveAllLobbyMembers(joinLobbyResponse.getName());
        });
    }

    /**
     * Method called when the logout button is pressed
     * <p>
     * This method is called when the logout button is pressed. It calls the
     * logout(user) method of the UserService to log out the user and posts an
     * instance of the ShowLoginViewEvent to the EventBus the SceneManager
     * is subscribed to.
     *
     * @param event The ActionEvent generated by pressing the logout button
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.auth.events.ShowLoginViewEvent
     * @see de.uol.swp.client.SceneManager
     * @see de.uol.swp.client.user.UserService
     * @since 2020-11-02
     */
    public void onLogoutButtonPressed(ActionEvent event) {
        userService.logout(loggedInUser);
        eventBus.post(showLoginViewMessage);
    }

    /**
     * Method called when the delete user button is pressed
     * <p>
     * This method is called when the delete user button is pressed. It first calls the
     * UserService to log the user out, then posts an instance of the
     * ShowLoginViewEvent to the EventBus the SceneManager is subscribed to, and finally
     * calls the UserService to drop the user.
     *
     * @param event The ActionEvent generated by pressing the delete user button
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.auth.events.ShowLoginViewEvent
     * @see de.uol.swp.client.SceneManager
     * @see de.uol.swp.client.user.UserService
     * @since 2020-11-20
     */
    public void onDeleteButtonPressed(ActionEvent event) {
        userService.logout(loggedInUser);
        eventBus.post(showLoginViewMessage);
        userService.dropUser(loggedInUser);
    }

    /**
     * Method called when the ChangePassword button is pressed
     * <p>
     * This method is called when the ChangePassword button is pressed.
     * It posts an instance of the ShowChangePasswordViewEvent to the EventBus the SceneManager is subscribed to.
     *
     * @param event The ActionEvent generated by pressing the ChangePassword button.
     * @author Eric Vuong
     * @see de.uol.swp.client.ChangePassword.event.ShowChangePasswordViewEvent
     * @see de.uol.swp.client.SceneManager
     * @since 2020-11-25
     */
    @FXML
    void onChangePasswordButtonPressed(ActionEvent event) {
        eventBus.post(new ShowChangePasswordViewEvent(loggedInUser));
    }

    /**
     * Method called when the SendMessageButton is pressed
     * <p>
     * This Method is called when the SendMessageButton is pressed. It calls the chatService
     * to create a new message with the contents of the messageField as its content and
     * the currently logged in user as author. It also clears the messageField.
     *
     * @param event the event
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see ChatService
     * @since 2020-12-17
     */
    @FXML
    public void onSendMessageButtonPressed(ActionEvent event) {
        String msg = messageField.getText();
        messageField.clear();
        chatService.newMessage(loggedInUser, msg);
    }


    /**
     * Method called when the DeleteMessageButton is pressed
     * <p>
     * This method is called when the DeleteMessageButton is pressed. It calls the chatService
     * to delete the message currently selected in the chatView.
     *
     * @param event the event
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see ChatService
     * @since 2020-12-17
     */
    @FXML
    public void onDeleteMessageButtonPressed(ActionEvent event) {
        Integer msgId = findId();
        if (msgId != null) {
            chatService.deleteMessage(msgId);
        }
    }

    /**
     * Method called when the EditMessageButton is pressed.
     * <p>
     * This method is called when the EditMessageButton is pressed. It calls the ChatService
     * to edit the message currently selected in the chatView by replacing the current content
     * with the content found in the messageField.
     *
     * @param event the event
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see ChatService
     * @since 2020-12-17
     */
    @FXML
    public void onEditMessageButtonPressed(ActionEvent event) {
        Integer msgId = findId();
        if (msgId != null) {
            chatService.editMessage(msgId, messageField.getText());
            messageField.clear();
        }
    }

    /**
     * Method to find the ID of a message in the chatView
     *
     * @return The ID of the message that was searched
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see MainMenuPresenter#chatMessageMap
     * @since 2020-12-17
     */
    private Integer findId() {
        String msgText = chatView.getSelectionModel().getSelectedItem();
        Integer msgId = null;
        for (Map.Entry<Integer, ChatMessage> entry : chatMessageMap.entrySet()) {
            final ChatMessage selectedMessage = entry.getValue();
            if (selectedMessage.toString().equals(msgText) && selectedMessage.getAuthor().equals(loggedInUser)) {
                msgId = entry.getKey();
                break;
            }
        }
        return msgId;
    }
}
