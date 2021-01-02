package de.uol.swp.client;

import com.google.inject.Inject;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.chat.message.CreatedChatMessageMessage;
import de.uol.swp.common.chat.message.DeletedChatMessageMessage;
import de.uol.swp.common.chat.message.EditedChatMessageMessage;
import de.uol.swp.common.chat.response.AskLatestChatMessageResponse;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * This class is the base for creating a new Presenter that uses some form of Chat function.
 * <p>
 * This class prepares the child classes to have the ChatService set and Chat-related methods
 * at the ready to reduce unnecessary code repetition.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.AbstractPresenter
 * @see de.uol.swp.client.main.MainMenuPresenter
 * @see de.uol.swp.client.lobby.LobbyPresenter
 * @since 2020-01-02
 */
public abstract class AbstractPresenterWithChat extends AbstractPresenter {

    protected static Logger LOG;

    @Inject
    protected ChatService chatService;

    protected String lobbyName;
    protected User loggedInUser;
    protected ObservableList<String> chatMessages;
    protected ObservableMap<Integer, ChatMessage> chatMessageMap;

    @FXML
    protected ListView<String> chatView;

    @FXML
    protected TextField messageField;

    /**
     * Called by the constructor of inheriting classes to set the Logger
     *
     * @param log the log of the inheriting class
     */
    public void init(Logger log) {
        LOG = log;
    }

    /**
     * Initialises the Presenter by calling {@code prepareChatVars()}.
     */
    @FXML
    protected void initialize() {
        prepareChatVars();
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
     * @see de.uol.swp.common.chat.message.CreatedChatMessageMessage
     * @see de.uol.swp.client.AbstractPresenterWithChat#chatMessageMap
     */
    protected void onCreatedChatMessageMessage(CreatedChatMessageMessage msg) {
        LOG.debug("Received Chat Message from " + msg.getMsg().getAuthor().getUsername()
                + ": '" + msg.getMsg().getContent() + " for Global chat");
        Platform.runLater(() -> chatMessageMap.put(msg.getMsg().getID(), msg.getMsg()));
    }

    /**
     * Handles incoming notification that a ChatMessage was deleted
     * <p>
     * If a DeletedChatMessageMessage is posted to the EventBus, this method
     * removes the ChatMessage with the corresponding ID from the chatMessageMap.
     *
     * @param msg The DeletedChatMessageMessage found on the EventBus
     * @see de.uol.swp.common.chat.message.DeletedChatMessageMessage
     * @see de.uol.swp.client.AbstractPresenterWithChat#chatMessageMap
     */
    protected void onDeletedChatMessageMessage(DeletedChatMessageMessage msg) {
        LOG.debug("Received instruction to delete ChatMessage with id " + msg.getId());
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
     * @see de.uol.swp.common.chat.message.EditedChatMessageMessage
     * @see de.uol.swp.client.AbstractPresenterWithChat#chatMessageMap
     */
    protected void onEditedChatMessageMessage(EditedChatMessageMessage msg) {
        LOG.debug("Received instruction to edit ChatMessage with id " + msg.getMsg().getID() + " to: '"
                + msg.getMsg().getContent() + '\'');
        Platform.runLater(() -> chatMessageMap.replace(msg.getMsg().getID(), msg.getMsg()));
    }

    /**
     * Handles AskLatestChatMessageResponse
     * <p>
     * If a AskLatestChatMessageResponse is found on the EventBus,
     * this method calls updateChatMessageList to fill or update the ChatMessageList.
     *
     * @param rsp The AskLatestChatMessageResponse found on the EventBus
     * @see de.uol.swp.common.chat.response.AskLatestChatMessageResponse
     */
    protected void onAskLatestChatMessageResponse(AskLatestChatMessageResponse rsp) {
        LOG.debug(rsp.getChatHistory());
        updateChatMessageList(rsp.getChatHistory());
    }

    /**
     * Method called when the DeleteMessageButton is pressed
     * <p>
     * This method is called when the DeleteMessageButton is pressed. It calls the chatService
     * to delete the message currently selected in the chatView.
     *
     * @param event The ActionEvent generated by pressing the Delete Message button
     * @see de.uol.swp.client.chat.ChatService
     */
    @FXML
    protected void onDeleteMessageButtonPressed(ActionEvent event) {
        Integer msgId = findId();
        if (msgId != null) {
            if (lobbyName != null) {
                System.out.println("Calling chatService.deleteMessage(" + msgId + ", " + lobbyName + ");");
                chatService.deleteMessage(msgId, lobbyName);
            } else {
                System.out.println("Calling chatService.deleteMessage(" + msgId + ");");
                chatService.deleteMessage(msgId);
            }
        }
    }

    /**
     * Method called when the SendMessageButton is pressed
     * <p>
     * This Method is called when the SendMessageButton is pressed. It calls the chatService
     * to create a new message with the contents of the messageField as its content and
     * the currently logged in user as author. It also clears the messageField.
     *
     * @param event The ActionEvent generated by pressing the Send Message button
     * @see de.uol.swp.client.chat.ChatService
     */
    @FXML
    protected void onSendMessageButtonPressed(ActionEvent event) {
        String msg = messageField.getText();
        messageField.clear();
        if (lobbyName != null) {
            LOG.debug("Sending ChatMessage for lobby " + lobbyName + " ('" + msg + "') from " + loggedInUser.getUsername());
            chatService.newMessage(loggedInUser, msg, lobbyName);
        } else {
            LOG.debug("Sending ChatMessage for MainMenu ('" + msg + "') from " + loggedInUser.getUsername());
            chatService.newMessage(loggedInUser, msg);
        }
    }

    /**
     * Method called when the EditMessageButton is pressed.
     * <p>
     * This method is called when the EditMessageButton is pressed. It calls the ChatService
     * to edit the message currently selected in the chatView by replacing the current content
     * with the content found in the messageField.
     *
     * @param event The ActionEvent generated by pressing the Edit Message button
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.chat.ChatService
     * @since 2020-12-17
     */
    @FXML
    protected void onEditMessageButtonPressed(ActionEvent event) {
        Integer msgId = findId();
        if (msgId != null) {
            if (lobbyName != null) {
                LOG.debug("Sending request to edit ChatMessage with ID: " + msgId + " in lobby " + lobbyName
                        + " to new content '" + messageField.getText() + '\'');
                chatService.editMessage(msgId, messageField.getText(), lobbyName);
            } else {
                LOG.debug("Sending request to edit ChatMessage with ID: " + msgId + " in Global chat to new content: '"
                        + messageField.getText() + "'");
                chatService.editMessage(msgId, messageField.getText());
            }
            messageField.clear();
        }
    }

    private void updateChatMessageList(List<ChatMessage> chatMessageList) {
        Platform.runLater(() -> {
            chatMessages.clear();
            chatMessageList.forEach(m -> chatMessageMap.put(m.getID(), m));
        });
    }

    /**
     * Method to find the ID of a message in the chatView
     *
     * @return The ID of the message that was searched
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.AbstractPresenterWithChat#chatMessageMap
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
     * Nulls the chatMessageMap and chatMessages variables
     * <p>
     * This method is called on pressing the logout or the delete account button, and
     * ensures that the chatMessageMap and chatMessages are reset to null, to avoid
     * multiple instances of the chat being displayed in the chatView.
     *
     * @author Finn Haase
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.AbstractPresenterWithChat#chatMessageMap
     * @see de.uol.swp.client.AbstractPresenterWithChat#chatMessages
     * @see de.uol.swp.client.AbstractPresenterWithChat#prepareChatVars()
     * @since 2020-12-26
     */
    protected void resetCharVars() {
        chatMessageMap = null;
        chatMessages = null;
    }
}
