package de.uol.swp.client.scene.util;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.jfoenix.utils.JFXUtilities;
import de.uol.swp.client.scene.SceneManager;
import de.uol.swp.client.sound.ISoundService;
import de.uol.swp.common.lobby.LobbyName;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * A utility class used for Presenter instantiation and Stage creation
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.scene.SceneManager
 * @since 2021-06-24
 */
public class PresenterAndStageHelper {

    private static final Logger LOG = LogManager.getLogger(PresenterAndStageHelper.class);

    @Inject
    private static Injector injector;
    @Inject
    @Named("styleSheet")
    private static String styleSheet;
    @Inject
    private static ISoundService soundService;

    /**
     * Subroutine creating parent panes from FXML files
     * <p>
     * This Method tries to create a parent pane from the FXML file specified by
     * the URL String given to it. If the LOG-Level is set to Debug or higher,
     * "Loading " is written to the LOG.
     * If it fails to load the view, a RuntimeException is thrown.
     *
     * @param fxmlFile FXML file to load the view from
     *
     * @return View loaded from FXML or null
     */
    public static Scene initPresenter(String fxmlFile) {
        Parent rootPane;
        FXMLLoader loader = injector.getInstance(FXMLLoader.class);
        try {
            URL url = SceneManager.class.getResource(fxmlFile);
            LOG.debug("Loading FXML-File {}", url);
            loader.setLocation(url);
            rootPane = loader.load();
            Scene returnScene = new Scene(rootPane);
            returnScene.getStylesheets().add(styleSheet);
            return returnScene;
        } catch (IOException e) {
            throw new RuntimeException("Could not load View!" + e.getMessage(), e);
        }
    }

    /**
     * Utility method used to instantiate and display a new Stage object with
     * the provided parameters
     * <p>
     * This method can be used for windows that don't require specified coordinates.
     *
     * @param primaryStage          The primary Stage created by JavaFX
     * @param fxmlPath              The path to the FXML file of the View to
     *                              display
     * @param title                 The title of the window
     * @param minHeight             The minimum height of the window
     * @param minWidth              The minimum width of the window
     * @param lobbyName             The LobbyName with which to associate the
     *                              new window
     * @param stageMap              The LobbyStageMap to store the Stage in
     * @param onCloseRequestHandler A WindowEvent Handler to be set for the
     *                              window's onCloseRequest
     * @param isRobberTaxStage      Whether the window to be opened is the Robber
     *                              Tax window or not
     * @param latch                 The CountDownLatch to use to signal the
     *                              SceneService that the window is ready
     */
    public static void makeAndShowStage(Stage primaryStage, String fxmlPath, String title, int minHeight, int minWidth,
                                        LobbyName lobbyName, Map<LobbyName, Stage> stageMap,
                                        EventHandler<WindowEvent> onCloseRequestHandler, boolean isRobberTaxStage,
                                        CountDownLatch latch) {
        makeAndShowStage(primaryStage, fxmlPath, title, minHeight, minWidth, null, null, lobbyName, stageMap,
                         onCloseRequestHandler, isRobberTaxStage, latch);
    }

    /**
     * Utility method used to instantiate and display a new Stage object with
     * the provided parameters
     * <p>
     * This method can be used for windows that require specified coordinates.
     *
     * @param primaryStage          The primary Stage created by JavaFX
     * @param fxmlPath              The path to the FXML file of the View to
     *                              display
     * @param title                 The title of the window
     * @param minHeight             The minimum height of the window
     * @param minWidth              The minimum width of the window
     * @param x                     The X coordinate of the new window
     * @param y                     The Y coordinate of the new window
     * @param lobbyName             The LobbyName with which to associate the
     *                              new window
     * @param stageMap              The LobbyStageMap to store the Stage in
     * @param onCloseRequestHandler A WindowEvent Handler to be set for the
     *                              window's onCloseRequest
     * @param isRobberTaxStage      Whether the window to be opened is the Robber
     *                              Tax window or not
     * @param latch                 The CountDownLatch to use to signal the
     *                              SceneService that the window is ready
     */
    public static void makeAndShowStage(Stage primaryStage, String fxmlPath, String title, int minHeight, int minWidth,
                                        Double x, Double y, LobbyName lobbyName, Map<LobbyName, Stage> stageMap,
                                        EventHandler<WindowEvent> onCloseRequestHandler, boolean isRobberTaxStage,
                                        CountDownLatch latch) {
        JFXUtilities.runInFX(() -> {
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setHeight(minHeight);
            stage.setMinHeight(minHeight);
            stage.setWidth(minWidth);
            stage.setMinWidth(minWidth);

            Scene scene = initPresenter(fxmlPath);

            stage.setScene(scene);
            stage.initModality(Modality.NONE);
            stage.initOwner(primaryStage);

            if (x != null) stage.setX(x);
            if (y != null) stage.setY(y);

            if (isRobberTaxStage) stage.initStyle(StageStyle.UNDECORATED);

            if (stageMap != null && lobbyName != null) stageMap.put(lobbyName, stage);

            if (onCloseRequestHandler != null) stage.setOnCloseRequest(onCloseRequestHandler);

            stage.show();

            if (latch != null) latch.countDown();
        });
    }

    /**
     * Utility method to display an Alert dialogue window
     *
     * @param title       The title of the Alert window
     * @param contentText The content of the Alert window
     * @param headerText  The text to be displayed in the header portion of the Alert window
     * @param confirmText The text of the "Confirm" button
     * @param alertType   What AlertType the Alert window should be
     */
    public static void showAlert(String title, String contentText, String headerText, String confirmText,
                                 Alert.AlertType alertType) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType, contentText);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            ButtonType confirm = new ButtonType(confirmText, ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(confirm);
            alert.getDialogPane().getStylesheets().add(styleSheet);
            alert.showAndWait();
            soundService.button();
        });
    }

    /**
     * Utility method to display an Alert dialogue window.
     * Returns true if OK is pressed and false if Cancel is pressed.
     *
     * @param title       The title of the Alert window
     * @param contentText The content of the Alert window
     * @param headerText  The text to be displayed in the header portion of the Alert window
     * @param confirmText The text of the "Confirm" button
     * @param alertType   What AlertType the Alert window should be
     */
    public static void showAndGetConfirmation(String title, String contentText, String headerText, String confirmText,
                                              String cancelText, Alert.AlertType alertType, Runnable AIDS) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType, contentText);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            ButtonType confirm = new ButtonType(confirmText, ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType(cancelText, ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(confirm, cancel);
            alert.getDialogPane().getStylesheets().add(styleSheet);
            Optional<ButtonType> result = alert.showAndWait();
            soundService.button();
            if (result.isPresent() && result.get() == confirm) {
                AIDS.run();
            }
        });
    }

    /**
     * Displays the provided Scene on the primary Stage with the provided
     * parameters
     *
     * @param scene     New scene to show
     * @param title     New window title
     * @param minWidth  Minimum Width of the scene
     * @param minHeight Minimum Height of the scene
     */
    public static void showSceneOnPrimaryStage(Stage primaryStage, Scene scene, String title, int minWidth,
                                               int minHeight) {
        Platform.runLater(() -> {
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(minWidth);
            primaryStage.setMinHeight(minHeight);
            primaryStage.setWidth(minWidth);
            primaryStage.setHeight(minHeight);
            primaryStage.show();
        });
    }

    /**
     * Utility method used to instantiate and display a new Stage object based
     * on a given Scene
     * <p>
     * This method can be used in cases where the Scene is persistently stored
     * but has to be displayed in a separate window from the primary Stage.
     *
     * @param primaryStage          The primary Stage created by JavaFX
     * @param title                 The title of the window
     * @param minHeight             The minimum height of the window
     * @param minWidth              The minimum width of the window
     * @param scene                 The Scene to display in the Stage object
     * @param onCloseRequestHandler A WindowEvent Handler to be set for the window's onCloseRequest
     */
    public static void showStageFromScene(Stage primaryStage, String title, int minHeight, int minWidth, Scene scene,
                                          EventHandler<WindowEvent> onCloseRequestHandler) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setHeight(minHeight);
            stage.setMinHeight(minHeight);
            stage.setWidth(minWidth);
            stage.setMinWidth(minWidth);

            stage.setScene(scene);
            stage.initModality(Modality.NONE);
            stage.initOwner(primaryStage);

            if (onCloseRequestHandler != null) stage.setOnCloseRequest(onCloseRequestHandler);

            stage.show();
        });
    }
}
