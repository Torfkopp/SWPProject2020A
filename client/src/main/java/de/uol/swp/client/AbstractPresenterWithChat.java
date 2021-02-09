package de.uol.swp.client;

import com.google.inject.Inject;
import de.uol.swp.client.chat.IChatService;
import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.chat.message.CreatedChatMessageMessage;
import de.uol.swp.common.chat.message.DeletedChatMessageMessage;
import de.uol.swp.common.chat.message.EditedChatMessageMessage;
import de.uol.swp.common.chat.response.AskLatestChatMessageResponse;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.Logger;

import java.util.List;

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
 * @since 2021-01-02
 */
public abstract class AbstractPresenterWithChat extends AbstractPresenter {

    protected static Logger LOG;

    @Inject
    protected IChatService chatService;

    protected String lobbyName;
    protected User loggedInUser;
    protected ObservableList<ChatMessage> chatMessages;

    @FXML
    protected ListView<ChatMessage> chatView;
    @FXML
    protected TextField messageField;

    /**
     * Called by the constructor of inheriting classes to set the Logger
     *
     * @param log The Logger of the inheriting class
     */
    public void init(Logger log) {
        LOG = log;
    }

    /**
     * Initialises the Presenter by calling {@code prepareChatVars()}.
     *
     * @implNote Called automatically by JavaFX
     */
    @FXML
    protected void initialize() {
        prepareChatVars();
    }

    /**
     * Handles AskLatestChatMessageResponse
     * <p>
     * If a AskLatestChatMessageResponse is found on the EventBus,
     * this method calls updateChatMessageList to fill or update the ChatMessageList.
     *
     * @param rsp The AskLatestChatMessageResponse found on the EventBus
     *
     * @see de.uol.swp.common.chat.response.AskLatestChatMessageResponse
     */
    protected void onAskLatestChatMessageResponse(AskLatestChatMessageResponse rsp) {
        if (rsp.getLobbyName() != null && rsp.getLobbyName().equals(this.lobbyName)) {
            LOG.debug("Latest ChatMessages for " + rsp.getLobbyName() + ": " + rsp.getChatHistory());
            updateChatMessageList(rsp.getChatHistory());
        } else if (rsp.getLobbyName() == null && this.lobbyName == null) {
            LOG.debug("Latest ChatMessages for Global chat: " + rsp.getChatHistory());
            updateChatMessageList(rsp.getChatHistory());
        }
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
     *
     * @see de.uol.swp.common.chat.message.CreatedChatMessageMessage
     */
    protected void onCreatedChatMessageMessage(CreatedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(this.lobbyName)) {
            LOG.debug("Received ChatMessage from " + msg.getMsg().getAuthor().getUsername() + ": '" + msg.getMsg()
                                                                                                         .getContent() + "' for " + msg
                              .getLobbyName() + " chat");
        } else if (!msg.isLobbyChatMessage() && this.lobbyName == null) {
            LOG.debug("Received ChatMessage from " + msg.getMsg().getAuthor().getUsername() + ": '" + msg.getMsg()
                                                                                                         .getContent() + "' for Global chat");
        }
        Platform.runLater(() -> chatMessages.add(msg.getMsg()));
    }

    /**
     * Method called when the DeleteMessageButton is pressed
     * <p>
     * This method is called when the DeleteMessageButton is pressed. It calls
     * the chatService to delete the message currently selected in the
     * chatView, but only when the ChatMessage author equals the logged in
     * user.
     *
     * @see de.uol.swp.client.chat.ChatService
     */
    @FXML
    protected void onDeleteMessageButtonPressed() {
        ChatMessage chatMsg = chatView.getSelectionModel().getSelectedItem();
        int msgId = chatMsg.getID();
        if (!chatMsg.getAuthor().equals(this.loggedInUser)) return;
        if (lobbyName != null) {
            LOG.debug("Requesting to delete ChatMessage with ID " + msgId + " from lobby " + lobbyName);
            chatService.deleteMessage(msgId, this.loggedInUser, lobbyName);
        } else {
            LOG.debug("Requesting to delete ChatMessage with ID " + msgId + "from Global chat");
            chatService.deleteMessage(msgId, this.loggedInUser);
        }
    }

    /**
     * Handles incoming notification that a ChatMessage was deleted
     * <p>
     * If a DeletedChatMessageMessage is posted to the EventBus, this method
     * removes the ChatMessage with the corresponding ID from the chatMessageMap.
     *
     * @param msg The DeletedChatMessageMessage found on the EventBus
     *
     * @see de.uol.swp.common.chat.message.DeletedChatMessageMessage
     */
    protected void onDeletedChatMessageMessage(DeletedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(this.lobbyName)) {
            LOG.debug("Received instruction to delete ChatMessage with ID " + msg.getId() + " in lobby " + msg
                    .getLobbyName());
        } else if (!msg.isLobbyChatMessage() && this.lobbyName == null) {
            LOG.debug("Received instruction to delete ChatMessage with ID " + msg.getId() + " in Global chat");
        }
        Platform.runLater(() -> {
            for (int i = 0; i < chatMessages.size(); i++) {
                if (chatMessages.get(i).getID() == msg.getId()) {
                    chatMessages.remove(i);
                    break;
                }
            }
        });
    }

    /**
     * Method called when the EditMessageButton is pressed.
     * <p>
     * This method is called when the EditMessageButton is pressed. It calls
     * the ChatService to edit the message currently selected in the chatView
     * by replacing the current content with the content found in the
     * messageField, but only when the ChatMessage author equals the logged in
     * user.
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.chat.ChatService
     * @since 2020-12-17
     */
    @FXML
    protected void onEditMessageButtonPressed() {
        ChatMessage chatMsg = chatView.getSelectionModel().getSelectedItem();
        int msgId = chatMsg.getID();
        if (!chatMsg.getAuthor().equals(this.loggedInUser)) return;
        if (lobbyName != null) {
            LOG.debug(
                    "Sending request to edit ChatMessage with ID " + msgId + " in lobby " + lobbyName + " to new content '" + messageField
                            .getText() + '\'');
            chatService.editMessage(msgId, messageField.getText(), this.loggedInUser, lobbyName);
        } else {
            LOG.debug(
                    "Sending request to edit ChatMessage with ID " + msgId + " in Global chat to new content: '" + messageField
                            .getText() + '\'');
            chatService.editMessage(msgId, messageField.getText(), this.loggedInUser);
        }
        messageField.clear();
    }

    /**
     * Handles incoming notification that a ChatMessage was edited
     * <p>
     * If an EditedChatMessageMessage is posted to the EventBus, this method
     * replaces the content in the chatMessageMap that is stored under the
     * edited ChatMessage's ID.
     *
     * @param msg The EditedChatMessageMessage found on the EventBus
     *
     * @see de.uol.swp.common.chat.message.EditedChatMessageMessage
     */
    protected void onEditedChatMessageMessage(EditedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(this.lobbyName)) {
            LOG.debug(
                    "Received instruction to edit ChatMessage with ID " + msg.getMsg().getID() + " to: '" + msg.getMsg()
                                                                                                               .getContent() + "' in lobby " + msg
                            .getLobbyName());
        } else if (!msg.isLobbyChatMessage() && this.lobbyName == null) {
            LOG.debug(
                    "Received instruction to edit ChatMessage with ID " + msg.getMsg().getID() + " to: '" + msg.getMsg()
                                                                                                               .getContent() + "' in Global Chat");
        }
        Platform.runLater(() -> {
            for (int i = 0; i < chatMessages.size(); i++) {
                if (chatMessages.get(i).getID() == msg.getMsg().getID()) {
                    chatMessages.set(i, msg.getMsg());
                }
            }
        });
    }

    /**
     * Method called when the SendMessageButton is pressed
     * <p>
     * This Method is called when the SendMessageButton is pressed. It calls the chatService
     * to create a new message with the contents of the messageField as its content and
     * the currently logged in user as author. It also clears the messageField.
     *
     * @see de.uol.swp.client.chat.ChatService
     */
    @FXML
    protected void onSendMessageButtonPressed() {
        String msg = messageField.getText();
        messageField.clear();
        if (lobbyName != null) {
            LOG.debug("Sending ChatMessage for lobby " + lobbyName + " ('" + msg + "') from " + loggedInUser
                    .getUsername());
            chatService.newMessage(loggedInUser, msg, lobbyName);
        } else {
            LOG.debug("Sending ChatMessage for Global chat ('" + msg + "') from " + loggedInUser.getUsername());
            chatService.newMessage(loggedInUser, msg);
        }
    }

    /**
     * Prepares the variables used for the chat storage and management
     * <p>
     * This method is called on a successful login and ensures that
     * the used variable chatMessages isn't null,
     * sets the items of the chatView to the chatMessages observableList,
     * and sets up the chatView.
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @since 2020-12-20
     */
    private void prepareChatVars() {
        if (chatMessages == null) chatMessages = FXCollections.observableArrayList();
        chatView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ChatMessage item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });
        chatView.setItems(chatMessages);
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
     * @see de.uol.swp.client.AbstractPresenterWithChat#chatMessages
     * @see de.uol.swp.client.AbstractPresenterWithChat#prepareChatVars()
     * @since 2020-12-26
     */
    protected void resetCharVars() {
        chatMessages = null;
    }

    private void updateChatMessageList(List<ChatMessage> chatMessageList) {
        Platform.runLater(() -> {
            if (chatMessages == null) prepareChatVars();
            chatMessages.clear();
            chatMessages.addAll(chatMessageList);
        });
    }
}
