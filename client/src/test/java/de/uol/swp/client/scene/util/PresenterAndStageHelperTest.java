package de.uol.swp.client.scene.util;

import com.google.inject.Injector;
import com.jfoenix.utils.JFXUtilities;
import de.uol.swp.client.specialisedUtil.LobbyStageMap;
import de.uol.swp.common.lobby.LobbyName;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
class PresenterAndStageHelperTest {

    private final Injector mockedInjector = mock(Injector.class);
    private final Stage primary = mock(Stage.class);
    private final LobbyName defaultLobby = mock(LobbyName.class);
    private final LobbyStageMap mockStageMap = mock(LobbyStageMap.class);
    private final EventHandler<WindowEvent> mockEventHandler = mock(EventHandler.class);
    private final CountDownLatch mockLatch = mock(CountDownLatch.class);
    private final Scene mockScene = mock(Scene.class);

    @Test
    void initPresenter() {
        try {
            FXMLLoader fxmlLoader = mock(FXMLLoader.class);
            //noinspection unchecked
            doReturn(fxmlLoader).when(mockedInjector).getInstance(isA(Class.class));
            doNothing().when(fxmlLoader).setLocation(isA(URL.class));
            Parent parent = new Pane();
            when(fxmlLoader.load()).thenReturn(parent);

            Scene expected = new Scene(new Pane());
            String styleSheet = "default";
            expected.getStylesheets().add(styleSheet);

            Field injectorField = PresenterAndStageHelper.class.getDeclaredField("injector");
            injectorField.setAccessible(true);
            injectorField.set(null, mockedInjector);
            Field styleSheetField = PresenterAndStageHelper.class.getDeclaredField("styleSheet");
            styleSheetField.setAccessible(true);
            styleSheetField.set(null, styleSheet);

            Scene actual = PresenterAndStageHelper.initPresenter("");

            verify(mockedInjector).getInstance(isA(Class.class));
            verify(fxmlLoader).load();
            verify(fxmlLoader).setLocation(isA(URL.class));
            // because Parents can only be root of one scene, this is all we can check
            assertIterableEquals(expected.getStylesheets(), actual.getStylesheets());
        } catch (IOException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void makeAndShowStage() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper
                    .makeAndShowStage(isA(Stage.class), isA(String.class), isA(String.class), isA(Integer.class),
                                      isA(Integer.class), isNull(), isNull(), isA(LobbyName.class),
                                      isA(LobbyStageMap.class), isA(EventHandler.class), isA(Boolean.class),
                                      isA(CountDownLatch.class))).then(invocation -> null);
            mockedHelper.when(() -> PresenterAndStageHelper
                    .makeAndShowStage(isA(Stage.class), isA(String.class), isA(String.class), isA(Integer.class),
                                      isA(Integer.class), isA(LobbyName.class), isA(LobbyStageMap.class),
                                      isA(EventHandler.class), isA(Boolean.class), isA(CountDownLatch.class)))
                        .thenCallRealMethod();

            PresenterAndStageHelper
                    .makeAndShowStage(primary, "fxml", "title", 1, 1, defaultLobby, mockStageMap, mockEventHandler,
                                      false, mockLatch);

            mockedHelper.verify(() -> PresenterAndStageHelper
                    .makeAndShowStage(eq(primary), eq("fxml"), eq("title"), eq(1), eq(1), isNull(), isNull(),
                                      eq(defaultLobby), eq(mockStageMap), eq(mockEventHandler), eq(false),
                                      eq(mockLatch)));
        }
    }

    @Test
    void makeAndShowStage_WithCoords() {
        try (MockedStatic<JFXUtilities> mockedUtilities = mockStatic(JFXUtilities.class)) {
            mockedUtilities.when(() -> JFXUtilities.runInFX(isA(Runnable.class))).then(i -> null);

            PresenterAndStageHelper
                    .makeAndShowStage(primary, "fxml", "title", 1, 1, 0.0, 0.0, defaultLobby, mockStageMap,
                                      mockEventHandler, false, mockLatch);

            /*cannot verify the executed runnable because object equality can't
              see that a Runnable declared here is functionally identical to the
              one declared in the real method*/
            mockedUtilities.verify(() -> JFXUtilities.runInFX(isA(Runnable.class)));
        }
    }

    @Test
    void showAlert() {
        try (MockedStatic<Platform> mockedPlatform = mockStatic(Platform.class)) {
            mockedPlatform.when(() -> Platform.runLater(isA(Runnable.class))).then(i -> null);

            PresenterAndStageHelper.showAlert("title", "content", "header", "confirm", Alert.AlertType.ERROR);

            /*cannot verify the executed runnable because object equality can't
              see that a Runnable declared here is functionally identical to the
              one declared in the real method*/
            mockedPlatform.verify(() -> Platform.runLater(isA(Runnable.class)));
        }
    }

    @Test
    void showAndGetConfirmation() {
        try (MockedStatic<Platform> mockedPlatform = mockStatic(Platform.class)) {
            mockedPlatform.when(() -> Platform.runLater(isA(Runnable.class))).then(i -> null);

            PresenterAndStageHelper.showAndGetConfirmation("title", "content", "header", "confirm", "cancel",
                                                           Alert.AlertType.CONFIRMATION, mock(Runnable.class));

            /*cannot verify the executed runnable because object equality can't
              see that a Runnable declared here is functionally identical to the
              one declared in the real method*/
            mockedPlatform.verify(() -> Platform.runLater(isA(Runnable.class)));
        }
    }

    @Test
    void showSceneOnPrimaryStage() {
        try (MockedStatic<Platform> mockedPlatform = mockStatic(Platform.class)) {
            mockedPlatform.when(() -> Platform.runLater(isA(Runnable.class))).then(i -> null);

            PresenterAndStageHelper.showSceneOnPrimaryStage(primary, mockScene, "title", 1, 1);

            /*cannot verify the executed runnable because object equality can't
              see that a Runnable declared here is functionally identical to the
              one declared in the real method*/
            mockedPlatform.verify(() -> Platform.runLater(isA(Runnable.class)));
        }
    }

    @Test
    void showStageFromScene() {
        try (MockedStatic<Platform> mockedPlatform = mockStatic(Platform.class)) {
            mockedPlatform.when(() -> Platform.runLater(isA(Runnable.class))).then(i -> null);

            PresenterAndStageHelper.showStageFromScene(primary, "title", 1, 1, mockScene, mockEventHandler);

            /*cannot verify the executed runnable because object equality can't
              see that a Runnable declared here is functionally identical to the
              one declared in the real method*/
            mockedPlatform.verify(() -> Platform.runLater(isA(Runnable.class)));
        }
    }
}