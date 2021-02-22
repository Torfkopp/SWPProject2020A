package de.uol.swp.client.devmenu;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.devmenu.request.DevMenuClassesRequest;
import de.uol.swp.common.devmenu.request.DevMenuCommandRequest;
import de.uol.swp.common.devmenu.response.DevMenuClassesResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class DevMenuPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/DevMenuView.fxml";
    private static final Logger LOG = LogManager.getLogger(DevMenuPresenter.class);
    private final List<TextField> textFields = new LinkedList<>();
    @FXML
    private ListView<String> classList;
    @FXML
    private ListView<Map<String, Class<?>>> constructorList;
    private ObservableList<String> classNameObservableList;
    private ObservableList<Map<String, Class<?>>> constructorObservableList;
    @FXML
    private HBox parameterBox;
    private Map<String, List<Map<String, Class<?>>>> classes;

    @FXML
    private void initialize() {
        eventBus.post(new DevMenuClassesRequest());
        if (classNameObservableList == null) classNameObservableList = FXCollections.observableArrayList();
        classList.setItems(classNameObservableList);
        if (constructorObservableList == null) constructorObservableList = FXCollections.observableArrayList();
        constructorList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Map<String, Class<?>> item, boolean empty) {
                super.updateItem(item, empty);
                StringBuilder txt = new StringBuilder();
                if (item != null) {
                    for (Map.Entry<String, Class<?>> arg : item.entrySet())
                        txt.append(", ").append(arg.getValue().getSimpleName()).append(": ").append(arg.getKey());
                    setText(txt.toString().length() > 2 ? txt.substring(2) : "");
                } else setText("");
            }
        });
        constructorList.setItems(constructorObservableList);
        LOG.debug("DevMenuPresenter initialised");
    }

    @Subscribe
    private void onDevMenuClassesResponse(DevMenuClassesResponse rsp) {
        classes = rsp.getClassesMap();
        classList.getSelectionModel().selectedItemProperty()
                 .addListener((observable, oldValue, newValue) -> updateConstructorList(classes.get(newValue)));

        updateClassList(rsp.getClassesMap().keySet());
    }

    @FXML
    private void onSendButtonPressed() {
        List<String> args = new LinkedList<>();
        for (TextField textField : textFields) args.add(textField.getText());
        eventBus.post(new DevMenuCommandRequest(classList.getSelectionModel().getSelectedItem(), args));
    }

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

    private void updateClassList(Set<String> classes) {
        classNameObservableList.clear();
        Platform.runLater(() -> {
            classNameObservableList.addAll(classes);
        });
    }

    private void updateConstructorList(List<Map<String, Class<?>>> constructors) {
        constructorObservableList.clear();
        Platform.runLater(() -> {
            constructorObservableList.addAll(constructors);
        });
        constructorList.getSelectionModel().selectedItemProperty()
                       .addListener(((observable, oldValue, newValue) -> updateArgumentFields(newValue)));
    }
}
