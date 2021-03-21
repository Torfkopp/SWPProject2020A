package de.uol.swp.client;

import com.google.inject.Inject;
import de.uol.swp.client.chat.IChatService;
import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.chat.ChatOrSystemMessage;
import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.chat.message.CreatedChatMessageMessage;
import de.uol.swp.common.chat.message.DeletedChatMessageMessage;
import de.uol.swp.common.chat.message.EditedChatMessageMessage;
import de.uol.swp.common.chat.response.AskLatestChatMessageResponse;
import de.uol.swp.common.chat.response.SystemMessageResponse;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
    protected ObservableList<ChatOrSystemMessage> chatMessages;

    @FXML
    protected ListView<ChatOrSystemMessage> chatView;
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
    protected void initialize() { prepareChatVars(); }

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
            LOG.debug("Received AskLatestChatMessageResponse for Lobby " + rsp.getLobbyName());
            updateChatMessageList(rsp.getChatHistory());
        } else if (rsp.getLobbyName() == null && this.lobbyName == null) {
            LOG.debug("Received AskLatestChatMessageResponse");
            updateChatMessageList(rsp.getChatHistory());
        }
    }

    /**
     * Handles new incoming ChatMessage
     * <p>
     * If a CreatedChatMessageMessage is posted to the EventBus, this method
     * places the incoming ChatMessage into the chatMessages list.
     * If the loglevel is set to DEBUG, the message "Received
     * CreatedChatMessageMessage" or "Received CreatedChatMessageMessage for
     * Lobby {@code <lobbyName>}" is displayed in the log.
     *
     * @param msg The CreatedChatMessageMessage found on the EventBus
     *
     * @implNote Some code inside this Method has to run in the JavaFX-application
     * thread. Therefore, it is crucial not to remove the {@code Platform.runLater()}
     * @see de.uol.swp.common.chat.message.CreatedChatMessageMessage
     */
    protected void onCreatedChatMessageMessage(CreatedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(this.lobbyName)) {
            LOG.debug("Received CreatedChatMessageMessage for Lobby " + msg.getLobbyName());
        } else if (!msg.isLobbyChatMessage() && this.lobbyName == null) {
            LOG.debug("Received CreatedChatMessageMessage");
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
        ChatOrSystemMessage chatOrSystemMessage = chatView.getSelectionModel().getSelectedItem();
        ChatMessage chatMsg;
        if (chatOrSystemMessage instanceof ChatMessage) chatMsg = (ChatMessage) chatOrSystemMessage;
        else return;
        int msgId = chatMsg.getID();
        if (!chatMsg.getAuthor().equals(this.loggedInUser)) return;
        if (lobbyName != null) {
            chatService.deleteMessage(msgId, this.loggedInUser, lobbyName);
        } else {
            chatService.deleteMessage(msgId, this.loggedInUser);
        }
    }

    /**
     * Handles incoming notification that a ChatMessage was deleted
     * <p>
     * If a DeletedChatMessageMessage is posted to the EventBus, this method
     * removes the ChatMessage with the corresponding ID from the chatMessages
     * list.
     * If the loglevel is set to DEBUG, the message "Received
     * DeletedChatMessageMessage" or "Received DeletedChatMessageMessage for
     * Lobby {@code <lobbyName>}" is displayed in the log.
     *
     * @param msg The DeletedChatMessageMessage found on the EventBus
     *
     * @implNote Some code inside this Method has to run in the JavaFX-application
     * thread. Therefore, it is crucial not to remove the {@code Platform.runLater()}
     * @see de.uol.swp.common.chat.message.DeletedChatMessageMessage
     */
    protected void onDeletedChatMessageMessage(DeletedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(this.lobbyName)) {
            LOG.debug("Received DeletedChatMessageMessage for Lobby " + msg.getLobbyName());
        } else if (!msg.isLobbyChatMessage() && this.lobbyName == null) {
            LOG.debug("Received DeletedChatMessageMessage");
        }
        Platform.runLater(() -> {
            for (int i = 0; i < chatMessages.size(); i++) {
                if (chatMessages.get(i) instanceof ChatMessage) {
                    ChatMessage chatMessage = (ChatMessage) chatMessages.get(i);
                    if (chatMessage.getID() == msg.getId()) {
                        chatMessages.remove(i);
                        break;
                    }
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
     * Should the selected ChatMessage be a SystemMessage, the method silently
     * returns with no further action.
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.chat.ChatService
     * @since 2020-12-17
     */
    @FXML
    protected void onEditMessageButtonPressed() {
        ChatOrSystemMessage chatOrSystemMessage = chatView.getSelectionModel().getSelectedItem();
        ChatMessage chatMsg;
        if (chatOrSystemMessage instanceof ChatMessage) chatMsg = (ChatMessage) chatOrSystemMessage;
        else return;
        int msgId = chatMsg.getID();
        if (!chatMsg.getAuthor().equals(this.loggedInUser)) return;
        if (lobbyName != null) {
            chatService.editMessage(msgId, messageField.getText(), this.loggedInUser, lobbyName);
        } else {
            chatService.editMessage(msgId, messageField.getText(), this.loggedInUser);
        }
        messageField.clear();
    }

    /**
     * Handles incoming notification that a ChatMessage was edited
     * <p>
     * If an EditedChatMessageMessage is posted to the EventBus, this method
     * replaces the ChatMessage with the corresponding ID in the chatMessages
     * list.
     * If the loglevel is set to DEBUG, the message "Received
     * EditedChatMessageMessage" or "Received EditedChatMessageMessage for
     * Lobby {@code <lobbyName>}" is displayed in the log.
     *
     * @param msg The EditedChatMessageMessage found on the EventBus
     *
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore, it is crucial not to remove the {@code Platform.runLater()}
     * @see de.uol.swp.common.chat.message.EditedChatMessageMessage
     */
    protected void onEditedChatMessageMessage(EditedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(this.lobbyName)) {
            LOG.debug("Received EditedChatMessageMessage for Lobby " + msg.getLobbyName());
        } else if (!msg.isLobbyChatMessage() && this.lobbyName == null) {
            LOG.debug("Received EditedChatMessageMessage");
        }
        Platform.runLater(() -> {
            for (int i = 0; i < chatMessages.size(); i++) {
                if (chatMessages.get(i) instanceof ChatMessage) {
                    ChatMessage chatMessage = (ChatMessage) chatMessages.get(i);
                    if (chatMessage.getID() == msg.getMsg().getID()) {
                        chatMessages.set(i, msg.getMsg());
                    }
                }
            }
        });
    }

    /**
     * Method called when the SendMessageButton is pressed
     * <p>
     * This Method is called when the SendMessageButton is pressed. It calls
     * the chatService to create a new message with the contents of the
     * messageField as its content and the currently logged in user as author.
     * It also clears the messageField.
     *
     * @see de.uol.swp.client.chat.ChatService
     */
    @FXML
    protected void onSendMessageButtonPressed() {
        String msg = messageField.getText();
        messageField.clear();
        if (lobbyName != null) {
            chatService.newMessage(loggedInUser, msg, lobbyName);
        } else {
            chatService.newMessage(loggedInUser, msg);
        }
    }

    /**
     * Handles new incoming SystemMessage
     * <p>
     * If a SystemMessageResponse is posted onto the EventBus, this method
     * places the incoming SystemMessage into the chatMessages list.
     * If the loglevel is set to DEBUG, the message "Received
     * SystemMessageResponse" or "Received SystemMessageResponse for Lobby
     * {@code <lobbyName>}" is displayed in the log.
     *
     * @param rsp The SystemMessageResponse found on the EventBus
     *
     * @since 2021-02-22
     */
    protected void onSystemMessageResponse(SystemMessageResponse rsp) {
        if (rsp.isLobbyChatMessage() && rsp.getLobbyName().equals(this.lobbyName)) {
            LOG.debug("Received SystemMessageResponse for Lobby " + rsp.getLobbyName());
        } else if (!rsp.isLobbyChatMessage() && this.lobbyName == null) LOG.debug("Received SystemMessageResponse");
        Platform.runLater(() -> chatMessages.add(rsp.getMsg()));
    }

    /**
     * Nulls the chatMessages variable
     * <p>
     * This method is called on pressing the logout or the delete account
     * button, and ensures that the chatMessages list is reset to null, to
     * avoid multiple instances of the chat being displayed in the chatView.
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
            protected void updateItem(ChatOrSystemMessage item, boolean empty) {
                super.updateItem(item, empty);
                if (item instanceof SystemMessage)
                    setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, Font.getDefault().getSize()));
                else setFont(Font.getDefault());
                setText(empty || item == null ? "" : item.toString());
                setMaxWidth(chatView.getWidth() - 5);
                setPrefWidth(chatView.getWidth() - 5);
                setWidth(chatView.getWidth() - 5);
                setWrapText(true);
            }
        });
        chatView.setItems(chatMessages);
    }

    /**
     * Helper method for updating the chatMessages list.
     * This method places the provided List of ChatMessage objects into the
     * chatMessages ObservableList.
     *
     * @param chatMessageList A List of ChatMessage objects to add to the chat
     *
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore, it is crucial not to remove the {@code Platform.runLater()}
     */
    private void updateChatMessageList(List<ChatMessage> chatMessageList) {
        Platform.runLater(() -> {
            if (chatMessages == null) prepareChatVars();
            chatMessages.clear();
            chatMessages.addAll(chatMessageList);
        });
    }
}
