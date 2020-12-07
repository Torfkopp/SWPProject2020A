package de.uol.swp.client.main;

import com.google.common.base.Charsets;
import com.google.common.eventbus.Subscribe;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.lobby.event.ShowLobbyViewEvent;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import de.uol.swp.common.lobby.response.AllOnlineLobbysResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Manages the main menu
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2019-08-29
 *
 */
@SuppressWarnings("UnstableApiUsage")
public class MainMenuPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/MainMenuView.fxml";

    private static final Logger LOG = LogManager.getLogger(MainMenuPresenter.class);

    private static final ShowLoginViewEvent showLoginViewMessage = new ShowLoginViewEvent();
    private static final ShowLobbyViewEvent showLobbyViewMessage = new ShowLobbyViewEvent();

    private ObservableList<String> users;

    private ObservableList<String> lobbys;

    private User loggedInUser;

    @Inject
    private LobbyService lobbyService;

    @FXML
    private ListView<String> usersView;

    @FXML
    private ListView<String> lobbyView;

    /**
     * Handles successful login
     *
     * If a LoginSuccessfulResponse is posted to the EventBus the loggedInUser
     * of this client is set to the one in the message received and the full
     * list of users currently logged in is requested.
     *
     * @param message the LoginSuccessfulResponse object seen on the EventBus
     * @see de.uol.swp.common.user.response.LoginSuccessfulResponse
     * @since 2019-09-05
     */
    @Subscribe
    public void loginSuccessful(LoginSuccessfulResponse message) {
        this.loggedInUser = message.getUser();
        userService.retrieveAllUsers();
    }

    /**
     * Handles new logged in users
     *
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
     *
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
     *
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
     * Updates the main menus user list according to the list given
     *
     * This method clears the entire user list and then adds the name of each user
     * in the list given to the main menus user list. If there ist no user list
     * this it creates one.
     *
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore it is crucial not to remove the {@code Platform.runLater()}
     * @param userList A list of UserDTO objects including all currently logged in
     *                 users
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
     * Handles new list of lobbys
     *
     * If a new AllLobbysResponse object is posted to the EventBus the names
     * of currently logged in users are put onto the lobby list in the main menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "Update of lobby
     * list" with the names of all currently existing lobbys is displayed in the
     * log.
     *
     * @param allLobbysResponse the AllLobbysResponse object seen on the EventBus
     * @see de.uol.swp.common.lobby.response.AllOnlineLobbysResponse
     * @since 2020-11-29
     */

    public void lobbyList(AllOnlineLobbysResponse allLobbysResponse) {
        updateLobbyList(allLobbysResponse.getName());
    }

    /**
     * Updates the main menus lobby list according to the list given
     *
     * This method clears the entire lobby list and then adds the name of each lobby
     * in the list given to the main menus lobby list. If there is no lobby list
     * this creates one.
     *
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore it is crucial not to remove the {@code Platform.runLater()}
     * @param lobbyList  A list of LobbyDTO objects including all currently existing
     * Lobbys
     * @see de.uol.swp.common.lobby.dto.LobbyDTO
     * @since 2020-11-29
     */

    private void updateLobbyList(List<LobbyDTO> lobbyList) {

        Platform.runLater(() -> {
            if (lobbys == null) {
                lobbys = FXCollections.observableArrayList();
                lobbyView.setItems(lobbys);
            }
            lobbys.clear();
            lobbyList.forEach(l -> lobbys.add(l.getName()));
        });
    }

    /**
     * Method called when the create lobby button is pressed
     *
     * If the create lobby button is pressed, this method requests the lobby service
     * to create a new lobby. This lobby will get a unique name and registers the user as its creator.
     *
     * @param event The ActionEvent created by pressing the create lobby button
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2019-11-20
     */
    @FXML
    void onCreateLobby(ActionEvent event) {
        //Creates a unique name by hashing the current time in milliseconds. Sorry (Marvin & Mario)
        String name = Hashing.sha256().hashString( "" + System.currentTimeMillis() , Charsets.UTF_8 ).toString();
        lobbyService.createNewLobby(name, (UserDTO) loggedInUser);
        eventBus.post(showLobbyViewMessage);
    }

    /**
     * Method called when the join lobby button is pressed
     *
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
        String lobbyname = "";

        lobbyView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        if(lobbyView.getSelectionModel().isEmpty()){
            System.out.println("leere Liste oder du hast nichts ausgewählt");
        }else{
            lobbyname = lobbyView.getSelectionModel().getSelectedItem();
        }

        lobbyService.joinLobby(lobbyname, new UserDTO(loggedInUser.getUsername(), "", ""));
    }

    /**
     * Method called when the logout button is pressed
     *
     * This method is called when the logout button is pressed. It calls the
     * logout(user) method of the UserService to log out the user and posts an
     * instance of the ShowLoginViewEvent to the EventBus the SceneManager
     * is subscribed to.
     *
     * @param event The ActionEvent generated by pressing the logout button
     *
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
     *
     * This method is called when the delete user button is pressed. It first calls the
     * UserService to log the user out, then posts an instance of the
     * ShowLoginViewEvent to the EventBus the SceneManager is subscribed to, and finally
     * calls the UserService to drop the user.
     * @param event The ActionEvent generated by pressing the delete user button
     * @see de.uol.swp.client.auth.events.ShowLoginViewEvent
     * @see de.uol.swp.client.SceneManager
     * @see de.uol.swp.client.user.UserService
     * @since 2020-11-20
     * @author Phillip-André Suhr
     */
    public void onDeleteButtonPressed(ActionEvent event) {
        userService.logout(loggedInUser);
        eventBus.post(showLoginViewMessage);
        userService.dropUser(loggedInUser);
    }
}
