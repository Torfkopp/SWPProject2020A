package de.uol.swp.client.devmenu;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.devmenu.CommandParser;
import de.uol.swp.common.devmenu.request.DevMenuClassesRequest;
import de.uol.swp.common.devmenu.request.DevMenuCommandRequest;
import de.uol.swp.common.devmenu.response.DevMenuClassesResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Manages the Developer Access Board
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-02-21
 */
@SuppressWarnings("UnstableApiUsage")
public class DevMenuPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/DevMenuView.fxml";
    private static final Logger LOG = LogManager.getLogger(DevMenuPresenter.class);
    private final List<TextField> textFields = new LinkedList<>();
    @FXML
    private TextField classFilterTextField;
    @FXML
    private ListView<String> classListView;
    @FXML
    private ListView<Map<String, Class<?>>> constructorList;
    @FXML
    private HBox parameterBox;

    private Map<String, List<Map<String, Class<?>>>> classes;
    private ObservableList<String> classNameObservableList;
    private FilteredList<String> filteredClassNameList;
    private ObservableList<Map<String, Class<?>>> constructorObservableList;
    private Map<String, Class<?>> currentlySelectedConstructor;

    /**
     * Initialises the DevMenu.
     * <p>
     * First, a {@code DevMenuClassesRequest} is posted onto the EventBus.
     * Then, a Listener is added to the classFilterTextField which will
     * filter for the text entered by the user.
     * The constructorList gets a CellFactory which handles the showing
     * of constructor args.
     *
     * @implNote Called automatically by JavaFX
     * @see de.uol.swp.common.devmenu.request.DevMenuClassesRequest
     */
    @FXML
    private void initialize() {
        eventBus.post(new DevMenuClassesRequest());
        if (classNameObservableList == null) classNameObservableList = FXCollections.observableArrayList();
        filteredClassNameList = new FilteredList<>(classNameObservableList, p -> true);

        classFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            parameterBox.getChildren().clear();
            filteredClassNameList.setPredicate(clsn -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return clsn.toLowerCase().contains(newValue.toLowerCase());
            });
        });
        classListView.setItems(new SortedList<>(filteredClassNameList));
        if (constructorObservableList == null) constructorObservableList = FXCollections.observableArrayList();
        constructorList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Map<String, Class<?>> item, boolean empty) {
                super.updateItem(item, empty);
                StringBuilder txt = new StringBuilder();
                if (item != null) {
                    if (item.isEmpty()) txt.append("  No arg constructor");
                    else for (Map.Entry<String, Class<?>> arg : item.entrySet())
                        txt.append(", ").append(arg.getValue().getSimpleName()).append(": ").append(arg.getKey());
                    setText(txt.toString().length() > 2 ? txt.substring(2) : "");
                } else setText("");
            }
        });
        constructorList.setItems(constructorObservableList);
        LOG.debug("DevMenuPresenter initialised");
    }

    /**
     * Handles a DevMenuClassesResponse found on the EventBus
     * <p>
     * If a new DevMenuClassesResponse is found on the EventBus, this
     * method adds a Listener to the classListView which will handle
     * updating the constructor argument ListView (on the right).
     * After that, {@code updateClassList} is called to display the
     * classes contained in the DevMenuClassesResponse.
     *
     * @param rsp The DevMenuClassesResponse found on the EventBus
     *
     * @see de.uol.swp.common.devmenu.response.DevMenuClassesResponse
     */
    @Subscribe
    private void onDevMenuClassesResponse(DevMenuClassesResponse rsp) {
        classes = rsp.getClassesMap();
        classListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                constructorObservableList.clear();
            } else updateConstructorList(classes.get(newValue));
        });
        updateClassList(rsp.getClassesMap().keySet());
    }

    /**
     * Method called when the Send Button is pressed.
     * <p>
     * This method is called when the Send Button is pressed.
     * TODO: explain the magix Temmo is doing
     *
     * @see de.uol.swp.common.devmenu.CommandParser
     */
    @FXML
    private void onSendButtonPressed() {
        List<CommandParser.ASTToken> args = new LinkedList<>();

        List<Class<?>> argTypes = new LinkedList<>();
        for (Map.Entry<String, Class<?>> entry : currentlySelectedConstructor.entrySet())
            argTypes.add(entry.getValue());
        for (int i = 0; i < textFields.size(); i++) {
            String text = textFields.get(i).getText();
            CommandParser.ASTToken.Union arg = new CommandParser.ASTToken.Union(text);
            CommandParser.ASTToken.Type type;
            switch (argTypes.get(i).getName()) {
                case "java.util.List":
                case "java.util.Set":
                case "java.util.Collection":
                    arg = new CommandParser.ASTToken.Union(CommandParser.parse(CommandParser.lex(text)));
                    type = CommandParser.ASTToken.Type.LIST;
                    break;
                case "java.util.Map":
                    type = CommandParser.ASTToken.Type.MAP;
                    break;
                default: //String, int, boolean
                    type = CommandParser.ASTToken.Type.UNTYPED;
                    break;
            }
            args.add(new CommandParser.ASTToken(type, arg));
        }
        LOG.debug("Sending DevMenuCommandRequest");
        eventBus.post(new DevMenuCommandRequest(classListView.getSelectionModel().getSelectedItem(), args));
    }

    /**
     * Helper method to dynamically generate Labels and TextField
     * for parameter input.
     *
     * @param args The Map of constructor arguments
     */
    private void updateArgumentFields(Map<String, Class<?>> args) {
        parameterBox.getChildren().clear();
        if (args == null) return;
        textFields.clear();
        VBox vBoxLeft = new VBox(5);
        VBox vBoxRight = new VBox(5);
        for (Map.Entry<String, Class<?>> arg : args.entrySet()) {
            Label label = new Label();
            label.setMinHeight(25);
            label.setText(arg.getKey() + ": " + arg.getValue().getSimpleName());
            vBoxLeft.getChildren().add(label);
            TextField txt = new TextField();
            textFields.add(txt);
            vBoxRight.getChildren().add(txt);
        }
        parameterBox.getChildren().addAll(vBoxLeft, vBoxRight);
    }

    /**
     * Helper method used to fill the class ListView (on the left)
     * with the class names provided by the DevMenuClassesResponse.
     *
     * @param classes The Set of class names to list
     *
     * @see de.uol.swp.common.devmenu.response.DevMenuClassesResponse
     */
    private void updateClassList(Set<String> classes) {
        classNameObservableList.clear();
        Platform.runLater(() -> classNameObservableList.addAll(classes));
    }

    /**
     * Helper method used to update the constructor ListView (on the
     * right) with the arguments of the currently selected class.
     *
     * @param constructors The List of constructors (each a Map of name and class)
     */
    private void updateConstructorList(List<Map<String, Class<?>>> constructors) {
        constructorObservableList.clear();
        parameterBox.getChildren().clear();
        Platform.runLater(() -> constructorObservableList.addAll(constructors));
        constructorList.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            currentlySelectedConstructor = newValue;
            if (newValue != null) updateArgumentFields(newValue);
        }));
    }
}
