package de.uol.swp.client;

import com.google.inject.Inject;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.client.main.MainMenuPresenter;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public abstract class AbstractPresenterWithChat extends AbstractPresenter{

    @Inject
    protected ChatService chatService;

    protected String lobbyName;
    protected User loggedInUser;

    protected static Logger LOG;
    protected ObservableList<String> chatMessages;
    protected ObservableMap<Integer, ChatMessage> chatMessageMap;

    @FXML
    protected ListView<String> chatView;

    @FXML
    protected TextField messageField;

    public void init(Logger log){
        LOG = log;
    }
    @FXML
    protected void initialize() {
        prepareChatVars();
    }

    protected void onCreatedChatMessageMessage(CreatedChatMessageMessage msg){
        LOG.debug("Received Chat Message from " + msg.getMsg().getAuthor().getUsername()
                + ": '" + msg.getMsg().getContent() + " for Global chat");
        Platform.runLater(() -> chatMessageMap.put(msg.getMsg().getID(), msg.getMsg()));
    }

    protected void onDeletedChatMessageMessage(DeletedChatMessageMessage msg) {
        LOG.debug("Received instruction to delete ChatMessage with id " + msg.getId());
        Platform.runLater(() -> chatMessageMap.remove(msg.getId()));
    }

    protected void onEditedChatMessageMessage(EditedChatMessageMessage msg){
        LOG.debug("Received instruction to edit ChatMessage with id " + msg.getMsg().getID() + " to: '"
                + msg.getMsg().getContent() + '\'');
        Platform.runLater(() -> chatMessageMap.replace(msg.getMsg().getID(), msg.getMsg()));
    }

    protected void onAskLatestChatMessageResponse(AskLatestChatMessageResponse msg){
        LOG.debug(msg.getChatHistory());
        updateChatMessageList(msg.getChatHistory());
    }

    @FXML
    protected void onDeleteMessageButtonPressed(ActionEvent event) {
        Integer msgId = findId();
        if (msgId != null) {
            if(lobbyName != null) {
                System.out.println("Calling chatService.deleteMessage(" + msgId + ", " + lobbyName + ");");
                chatService.deleteMessage(msgId, lobbyName);
            } else{
                System.out.println("Calling chatService.deleteMessage(" + msgId + ");");
                chatService.deleteMessage(msgId);
            }
        }
    }

    @FXML
    protected void onSendMessageButtonPressed(ActionEvent event) {
        String msg = messageField.getText();
        messageField.clear();
        if(lobbyName != null) {
            LOG.debug("Sending ChatMessage for lobby " + lobbyName + " ('" + msg + "') from " + loggedInUser.getUsername());
            chatService.newMessage(loggedInUser, msg, lobbyName);
        }else{
            LOG.debug("Sending ChatMessage for MainMenu ('" + msg + "') from " + loggedInUser.getUsername());
            chatService.newMessage(loggedInUser, msg, lobbyName);
        }
    }

    @FXML
    protected void onEditMessageButtonPressed(ActionEvent event) {
        Integer msgId = findId();
        if (msgId != null) {
            if(lobbyName != null) {
                System.out.println("Calling chatService.editMessage(" + msgId + ", " + messageField.getText() + ", " + lobbyName + ");");
                chatService.editMessage(msgId, messageField.getText(), lobbyName);
            }else{
                System.out.println("Calling chatService.editMessage(" + msgId + ", " + messageField.getText() + ");");
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
     * @see de.uol.swp.client.main.MainMenuPresenter#chatMessageMap
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
     * @see de.uol.swp.client.main.MainMenuPresenter#chatMessageMap
     * @see de.uol.swp.client.main.MainMenuPresenter#chatMessages
     * @see de.uol.swp.client.main.MainMenuPresenter#prepareChatVars()
     * @since 2020-12-26
     */
    protected void resetCharVars() {
        chatMessageMap = null;
        chatMessages = null;
    }
}
