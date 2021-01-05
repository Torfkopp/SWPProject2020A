package de.uol.swp.client.main;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenterWithChat;
import de.uol.swp.client.ChangePassword.event.ShowChangePasswordViewEvent;
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
import de.uol.swp.client.lobby.event.LobbyErrorEvent;
import de.uol.swp.client.lobby.event.ShowLobbyViewEvent;
import de.uol.swp.common.chat.message.CreatedChatMessageMessage;
import de.uol.swp.common.chat.message.DeletedChatMessageMessage;
import de.uol.swp.common.chat.message.EditedChatMessageMessage;
import de.uol.swp.common.chat.response.AskLatestChatMessageResponse;
import de.uol.swp.common.lobby.message.LobbyCreatedMessage;
import de.uol.swp.common.lobby.message.LobbyDeletedMessage;
import de.uol.swp.common.lobby.response.AllLobbiesResponse;
import de.uol.swp.common.lobby.response.CreateLobbyResponse;
import de.uol.swp.common.lobby.response.JoinLobbyResponse;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.Optional;

/**
 * Manages the main menu
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.AbstractPresenter
 * @see de.uol.swp.client.AbstractPresenterWithChat
 * @since 2019-08-29
 */
@SuppressWarnings("UnstableApiUsage")
public class MainMenuPresenter extends AbstractPresenterWithChat {

    public static final String fxml = "/fxml/MainMenuView.fxml";

    private static final ShowLoginViewEvent showLoginViewMessage = new ShowLoginViewEvent();

    private ObservableList<String> users;

    private ObservableList<String> lobbies;

    @FXML
    private ListView<String> lobbyView;

    @FXML
    private ListView<String> usersView;

    /**
     * Constructor
     * <p>
     * This constructor calls the init method of the AbstractPresenterWithChat
     * to set the appropriate Logger.
     *
     * @since 2021-01-02
     */
    public MainMenuPresenter() {
        super.init(LogManager.getLogger(MainMenuPresenter.class));
    }

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
        this.loggedInUser = message.getUser();
        userService.retrieveAllUsers();
        lobbyService.retrieveAllLobbies();
        chatService.askLatestMessages(10);
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

    @Override
    @Subscribe
    public void onCreatedChatMessageMessage(CreatedChatMessageMessage msg) {
        if (!msg.isLobbyChatMessage()) {
            super.onCreatedChatMessageMessage(msg);
        }
    }

    @Override
    @Subscribe
    public void onDeletedChatMessageMessage(DeletedChatMessageMessage msg) {
        if (!msg.isLobbyChatMessage()) {
            super.onDeletedChatMessageMessage(msg);
        }
    }

    @Override
    @Subscribe
    public void onEditedChatMessageMessage(EditedChatMessageMessage msg) {
        if (!msg.isLobbyChatMessage()) {
            super.onEditedChatMessageMessage(msg);
        }
    }

    @Override
    @Subscribe
    public void onAskLatestChatMessageResponse(AskLatestChatMessageResponse msg) {
        if (msg.getLobbyName() == null) {
            super.onAskLatestChatMessageResponse(msg);
        }
    }

    /**
     * Updates the main menus user list according to the list given
     * <p>
     * This method clears the entire user list and then adds the name of each user
     * in the list given to the main menus user list. If there ist no user list
     * this it creates one.
     *
     * @param userList A list of UserDTO objects including all currently logged in
     *                 users
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore it is crucial not to remove the {@code Platform.runLater()}
     * @see de.uol.swp.common.user.UserDTO
     * @since 2019-08-29
     */
    private void updateUsersList(List<UserDTO> userList) {
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
     * @see de.uol.swp.common.lobby.message.LobbyCreatedMessage
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
     * @see de.uol.swp.common.lobby.response.CreateLobbyResponse
     * @see de.uol.swp.client.lobby.event.ShowLobbyViewEvent
     * @see de.uol.swp.client.lobby.LobbyService#retrieveAllLobbyMembers(String)
     * @since 2020-12-20
     */
    @Subscribe
    public void onCreateLobbyResponse(CreateLobbyResponse createLobbyResponse) {
        Platform.runLater(() -> {
            eventBus.post(new ShowLobbyViewEvent(createLobbyResponse.getName()));
            lobbyService.retrieveAllLobbyMembers(createLobbyResponse.getName());
            lobbyService.refreshLobbyPresenterFields(createLobbyResponse.getName(), loggedInUser);
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
     * @see de.uol.swp.common.lobby.message.LobbyDeletedMessage
     * @since 2020-12-17
     */
    @Subscribe
    private void onLobbyDeletedMessage(LobbyDeletedMessage msg) {
        if (msg.getName() == null || msg.getName().isEmpty()) {
            LOG.debug("Tried to delete Lobby without name from LobbyList ");
        } else {
            Platform.runLater(() -> lobbies.remove(msg.getName()));
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
        updateLobbyList(allLobbiesResponse.getLobbies());
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
        result.ifPresent(s -> lobbyService.createNewLobby(s, (UserDTO) loggedInUser));
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
        lobbyView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        if (lobbyView.getSelectionModel().isEmpty()) {
            eventBus.post(new LobbyErrorEvent("Please choose a valid Lobby"));
        } else {
            String lobbyName = lobbyView.getSelectionModel().getSelectedItem();
            lobbyService.joinLobby(lobbyName, (UserDTO) loggedInUser);
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
     * @see de.uol.swp.common.lobby.response.JoinLobbyResponse
     * @see de.uol.swp.client.lobby.event.ShowLobbyViewEvent
     * @see de.uol.swp.client.lobby.LobbyService#retrieveAllLobbyMembers(String)
     * @since 2020-12-20
     */
    @Subscribe
    public void onJoinLobbyResponse(JoinLobbyResponse joinLobbyResponse) {
        Platform.runLater(() -> {
            eventBus.post(new ShowLobbyViewEvent(joinLobbyResponse.getName()));
            lobbyService.retrieveAllLobbyMembers(joinLobbyResponse.getName());
            lobbyService.refreshLobbyPresenterFields(joinLobbyResponse.getName(), loggedInUser);
        });
    }

    /**
     * Method called when the logout button is pressed
     * <p>
     * This method is called when the logout button is pressed. It calls the
     * logout(user) method of the UserService to log out the user, resets the
     * variables used for storing the chat history, and then posts an
     * instance of the ShowLoginViewEvent to the EventBus the SceneManager
     * is subscribed to.
     *
     * @param event The ActionEvent generated by pressing the logout button
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.AbstractPresenterWithChat#resetCharVars()
     * @see de.uol.swp.client.auth.events.ShowLoginViewEvent
     * @see de.uol.swp.client.SceneManager
     * @see de.uol.swp.client.user.UserService
     * @since 2020-11-02
     */
    public void onLogoutButtonPressed(ActionEvent event) {
        userService.logout(loggedInUser);
        resetCharVars();
        eventBus.post(showLoginViewMessage);
    }

    /**
     * Method called when the delete user button is pressed
     * <p>
     * This method is called when the delete user button is pressed. It first
     * calls the UserService to log the user out, resets the variables used
     * for storing the chat history, and then posts an instance of the
     * ShowLoginViewEvent to the EventBus the SceneManager is subscribed to,
     * and finally calls the UserService to drop the user.
     *
     * @param event The ActionEvent generated by pressing the delete user button
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.AbstractPresenterWithChat#resetCharVars()
     * @see de.uol.swp.client.auth.events.ShowLoginViewEvent
     * @see de.uol.swp.client.SceneManager
     * @see de.uol.swp.client.user.UserService
     * @since 2020-11-20
     */
    public void onDeleteButtonPressed(ActionEvent event) {
        userService.logout(loggedInUser);
        resetCharVars();
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
}
