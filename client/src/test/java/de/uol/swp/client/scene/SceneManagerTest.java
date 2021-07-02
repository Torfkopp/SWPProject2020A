package de.uol.swp.client.scene;

import com.google.common.eventbus.EventBus;
import de.uol.swp.client.auth.LoginPresenter;
import de.uol.swp.client.changeAccountDetails.ChangeAccountDetailsPresenter;
import de.uol.swp.client.changeSettings.ChangeSettingsPresenter;
import de.uol.swp.client.devmenu.DevMenuPresenter;
import de.uol.swp.client.lobby.LobbyPresenter;
import de.uol.swp.client.lobby.RobberTaxPresenter;
import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.client.register.RegistrationPresenter;
import de.uol.swp.client.rules.RulesOverviewPresenter;
import de.uol.swp.client.scene.event.SetAcceleratorsEvent;
import de.uol.swp.client.scene.util.PresenterAndStageHelper;
import de.uol.swp.client.sound.ISoundService;
import de.uol.swp.client.specialisedUtil.LobbyStageMap;
import de.uol.swp.client.trade.*;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;
import de.uol.swp.common.user.User;
import de.uol.swp.common.util.ResourceManager;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"UnstableApiUsage", "unchecked"})
class SceneManagerTest {

    private final ISoundService soundService = mock(ISoundService.class);
    private final ITradeService tradeService = mock(ITradeService.class);
    private final EventBus eventBus = mock(EventBus.class);
    private final Stage primary = mock(Stage.class);
    private final LobbyName defaultLobby = new LobbyName("chubby bunny");
    private final Stage mockStage = mock(Stage.class);
    private final CountDownLatch mockLatch = mock(CountDownLatch.class);
    private final Actor mockActor = mock(Actor.class);
    private final User mockUser = mock(User.class);

    @BeforeEach
    protected void setUp() {
        doNothing().when(primary).close();
        doReturn("test").when(mockActor).toString();
        doReturn("test").when(mockUser).toString();
    }

    @Test
    void closeAcceptTradeWindow() {
        try (MockedStatic<PresenterAndStageHelper> mockedStatic = mockStatic(PresenterAndStageHelper.class)) {
            mockedStatic.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            try (MockedConstruction<LobbyStageMap> ignored = mockConstruction(LobbyStageMap.class, (mock, context) -> {
                doNothing().when(mock).close(isA(LobbyName.class));
                doCallRealMethod().when(mock).put(isA(LobbyName.class), isA(Stage.class));
                doCallRealMethod().when(mock).isEmpty();
                doCallRealMethod().when(mock).containsKey(isA(LobbyName.class));
            })) {
                SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

                Field acceptTradeStagesField = sceneManager.getClass().getDeclaredField("tradingResponseStages");
                acceptTradeStagesField.setAccessible(true);
                assertTrue(acceptTradeStagesField.get(sceneManager) instanceof LobbyStageMap);
                LobbyStageMap tradingResponseStages = (LobbyStageMap) acceptTradeStagesField.get(sceneManager);

                tradingResponseStages.put(defaultLobby, mockStage);

                sceneManager.closeAcceptTradeWindow(defaultLobby);

                verify(tradingResponseStages).close(defaultLobby);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    void closeLobbies() {
        LobbyName secondLobby = new LobbyName("bunny");
        try (MockedStatic<PresenterAndStageHelper> mockedStatic = mockStatic(PresenterAndStageHelper.class)) {
            mockedStatic.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            try (MockedConstruction<LobbyStageMap> ignored = mockConstruction(LobbyStageMap.class, (mock, context) -> {
                doNothing().when(mock).close(isA(LobbyName.class));
                doCallRealMethod().when(mock).put(isA(LobbyName.class), isA(Stage.class));
                doCallRealMethod().when(mock).keySet();
            })) {
                SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

                Field lobbyStageField = sceneManager.getClass().getDeclaredField("lobbyStages");
                lobbyStageField.setAccessible(true);
                assertTrue(lobbyStageField.get(sceneManager) instanceof LobbyStageMap);
                LobbyStageMap lobbyStages = (LobbyStageMap) lobbyStageField.get(sceneManager);
                lobbyStages.put(defaultLobby, mockStage);
                lobbyStages.put(secondLobby, mockStage);

                sceneManager.closeLobbies();

                verify(lobbyStages).keySet();
                verify(lobbyStages, times(2)).close(isA(LobbyName.class));
                verify(lobbyStages).close(defaultLobby);
                verify(lobbyStages).close(secondLobby);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    void closeMainScreen() {
        try (MockedStatic<PresenterAndStageHelper> mockedStatic = mockStatic(PresenterAndStageHelper.class)) {
            mockedStatic.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            try (MockedStatic<Platform> mockedPlatform = mockStatic(Platform.class)) {
                mockedPlatform.when(() -> Platform.runLater(isA(Runnable.class))).then(i -> {
                    primary.close();
                    return null;
                });
                SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

                sceneManager.closeMainScreen();

                mockedPlatform.verify(() -> Platform.runLater(any(Runnable.class)));
                verify(primary).close();
            }
        }
    }

    @Test
    void closeRobberTaxWindow() {
        try (MockedStatic<PresenterAndStageHelper> mockedStatic = mockStatic(PresenterAndStageHelper.class)) {
            mockedStatic.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            try (MockedConstruction<LobbyStageMap> ignored = mockConstruction(LobbyStageMap.class, (mock, context) -> {
                doNothing().when(mock).close(isA(LobbyName.class));
                doCallRealMethod().when(mock).put(isA(LobbyName.class), isA(Stage.class));
                doCallRealMethod().when(mock).isEmpty();
                doCallRealMethod().when(mock).containsKey(isA(LobbyName.class));
                doReturn(mockStage).when(mock).get(isA(LobbyName.class));
                doCallRealMethod().when(mock).remove(isA(LobbyName.class), isA(Stage.class));
            })) {
                try (MockedStatic<Platform> mockedPlatform = mockStatic(Platform.class)) {
                    mockedPlatform.when(() -> Platform.runLater(isA(Runnable.class))).then(i -> {
                        mockStage.close();
                        return null;
                    });
                    SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

                    Field robberTaxStages = sceneManager.getClass().getDeclaredField("robberTaxStages");
                    robberTaxStages.setAccessible(true);
                    assertTrue(robberTaxStages.get(sceneManager) instanceof LobbyStageMap);
                    LobbyStageMap robberStages = (LobbyStageMap) robberTaxStages.get(sceneManager);
                    robberStages.put(defaultLobby, mockStage);

                    sceneManager.closeRobberTaxWindow(defaultLobby);

                    verify(robberStages).containsKey(defaultLobby);
                    verify(robberStages).get(defaultLobby);
                    mockedPlatform.verify(() -> Platform.runLater(isA(Runnable.class)));
                    verify(mockStage).close();
                    verify(robberStages).remove(defaultLobby, mockStage);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    void closeTradeWindow() {
        try (MockedStatic<PresenterAndStageHelper> mockedStatic = mockStatic(PresenterAndStageHelper.class)) {
            mockedStatic.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            try (MockedConstruction<LobbyStageMap> ignored = mockConstruction(LobbyStageMap.class, (mock, context) -> {
                doNothing().when(mock).close(isA(LobbyName.class));
                doCallRealMethod().when(mock).put(isA(LobbyName.class), isA(Stage.class));
                doCallRealMethod().when(mock).isEmpty();
                doCallRealMethod().when(mock).containsKey(isA(LobbyName.class));
            })) {
                SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

                Field tradingStagesField = sceneManager.getClass().getDeclaredField("tradingStages");
                tradingStagesField.setAccessible(true);
                assertTrue(tradingStagesField.get(sceneManager) instanceof LobbyStageMap);
                LobbyStageMap tradingStages = (LobbyStageMap) tradingStagesField.get(sceneManager);

                tradingStages.put(defaultLobby, mockStage);

                sceneManager.closeTradeWindow(defaultLobby);

                verify(tradingStages).close(defaultLobby);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    void initViews() {
        try (MockedStatic<PresenterAndStageHelper> mockedStatic = mockStatic(PresenterAndStageHelper.class)) {
            mockedStatic.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            try (MockedConstruction<LobbyStageMap> mockedLobbyStageMaps = mockConstruction(LobbyStageMap.class)) {
                new SceneManager(soundService, eventBus, primary, tradeService);

                assertEquals(5, mockedLobbyStageMaps.constructed().size());
                mockedStatic.verify(() -> PresenterAndStageHelper.initPresenter(LoginPresenter.fxml));
                mockedStatic.verify(() -> PresenterAndStageHelper.initPresenter(MainMenuPresenter.fxml));
                mockedStatic.verify(() -> PresenterAndStageHelper.initPresenter(RegistrationPresenter.fxml));
                mockedStatic.verify(() -> PresenterAndStageHelper.initPresenter(RulesOverviewPresenter.fxml));
                mockedStatic.verify(() -> PresenterAndStageHelper.initPresenter(ChangeAccountDetailsPresenter.fxml));
                mockedStatic.verify(() -> PresenterAndStageHelper.initPresenter(ChangeSettingsPresenter.fxml));
                verify(eventBus).post(isA(SetAcceleratorsEvent.class));
            }
        }
    }

    @Test
    void showAcceptTradeWindow() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .makeAndShowStage(isA(Stage.class), isA(String.class), isA(String.class), isA(Integer.class),
                                      isA(Integer.class), isA(LobbyName.class), isA(LobbyStageMap.class),
                                      isA(EventHandler.class), isA(Boolean.class), isA(CountDownLatch.class)))
                        .then(invocation -> null);
            String fxml = TradeWithUserAcceptPresenter.fxml;
            String title = ResourceManager.get("game.trade.window.receiving.title", mockActor);
            int minHeight = TradeWithUserAcceptPresenter.MIN_HEIGHT;
            int minWidth = TradeWithUserAcceptPresenter.MIN_WIDTH;

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            sceneManager.showAcceptTradeWindow(defaultLobby, mockActor, mockLatch);

            mockedHelper.verify(() -> PresenterAndStageHelper
                    .makeAndShowStage(eq(primary), eq(fxml), eq(title), eq(minHeight), eq(minWidth), eq(defaultLobby),
                                      isA(LobbyStageMap.class), isA(EventHandler.class), eq(false), eq(mockLatch)));
        }
    }

    @Test
    void showBankTradeWindow() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .makeAndShowStage(isA(Stage.class), isA(String.class), isA(String.class), isA(Integer.class),
                                      isA(Integer.class), isA(LobbyName.class), isA(LobbyStageMap.class),
                                      isA(EventHandler.class), isA(Boolean.class), isA(CountDownLatch.class)))
                        .then(invocation -> null);
            String fxml = TradeWithBankPresenter.fxml;
            String title = ResourceManager.get("game.trade.window.bank.title");
            int minHeight = TradeWithBankPresenter.MIN_HEIGHT;
            int minWidth = TradeWithBankPresenter.MIN_WIDTH;

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            sceneManager.showBankTradeWindow(defaultLobby, mockLatch);

            mockedHelper.verify(() -> PresenterAndStageHelper
                    .makeAndShowStage(eq(primary), eq(fxml), eq(title), eq(minHeight), eq(minWidth), eq(defaultLobby),
                                      isA(LobbyStageMap.class), isA(EventHandler.class), eq(false), eq(mockLatch)));
        }
    }

    @Test
    void showChangeAccountDetailsScreen() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .showSceneOnPrimaryStage(isA(Stage.class), isA(Scene.class), isA(String.class), isA(Integer.class),
                                             isA(Integer.class))).then(invocation -> null);
            String title = ResourceManager.get("changeaccdetails.window.title");
            int minHeight = ChangeAccountDetailsPresenter.MIN_HEIGHT;
            int minWidth = ChangeAccountDetailsPresenter.MIN_WIDTH;

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            sceneManager.showChangeAccountDetailsScreen(mockUser);

            mockedHelper.verify(() -> PresenterAndStageHelper
                    .showSceneOnPrimaryStage(eq(primary), isA(Scene.class), eq(title), eq(minWidth), eq(minHeight)));
            verify(primary).setOnCloseRequest(isA(EventHandler.class));
        }
    }

    @Test
    void showChangeSettingsScreen() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .showSceneOnPrimaryStage(isA(Stage.class), isA(Scene.class), isA(String.class), isA(Integer.class),
                                             isA(Integer.class))).then(invocation -> null);
            String title = ResourceManager.get("changeproperties.window.title");
            int minHeight = ChangeSettingsPresenter.MIN_HEIGHT;
            int minWidth = ChangeSettingsPresenter.MIN_WIDTH;

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            sceneManager.showChangeSettingsScreen(mockUser);

            mockedHelper.verify(() -> PresenterAndStageHelper
                    .showSceneOnPrimaryStage(eq(primary), isA(Scene.class), eq(title), eq(minWidth), eq(minHeight)));
            verify(primary).setOnCloseRequest(isA(EventHandler.class));
        }
    }

    @Test
    void showDevMenuWindow() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .makeAndShowStage(isA(Stage.class), isA(String.class), isA(String.class), isA(Integer.class),
                                      isA(Integer.class), isA(Double.class), isA(Double.class), isA(LobbyName.class),
                                      isA(LobbyStageMap.class), isA(EventHandler.class), isA(Boolean.class),
                                      isA(Boolean.class), isA(CountDownLatch.class))).then(invocation -> null);
            doReturn(11.0).when(primary).getX();
            doReturn(10.0).when(primary).getY();
            String fxml = DevMenuPresenter.fxml;
            String title = ResourceManager.get("devmenu.window.title");
            int minHeight = DevMenuPresenter.MIN_HEIGHT;
            int minWidth = DevMenuPresenter.MIN_WIDTH;
            double expectedXPos = 100.0 + primary.getX();
            double expectedYPos = primary.getY();

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            Field devMenuOpenField = sceneManager.getClass().getDeclaredField("devMenuIsOpen");
            devMenuOpenField.setAccessible(true);
            devMenuOpenField.set(sceneManager, true);

            sceneManager.showDevMenuWindow();

            mockedHelper.verify(() -> PresenterAndStageHelper
                    .makeAndShowStage(isA(Stage.class), isA(String.class), isA(String.class), isA(Integer.class),
                                      isA(Integer.class), isA(Double.class), isA(Double.class), isA(LobbyName.class),
                                      isA(LobbyStageMap.class), isA(EventHandler.class), isA(Boolean.class), eq(false),
                                      isA(CountDownLatch.class)), times(0));

            devMenuOpenField.set(sceneManager, false);

            sceneManager.showDevMenuWindow();

            mockedHelper.verify(() -> PresenterAndStageHelper
                    .makeAndShowStage(eq(primary), eq(fxml), eq(title), eq(minHeight), eq(minWidth), eq(expectedXPos),
                                      eq(expectedYPos), isNull(), isNull(), isA(EventHandler.class), eq(false),
                                      eq(false), isNull()));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void showError() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .showAlert(isA(String.class), isA(String.class), isA(String.class), isA(String.class),
                               isA(Alert.AlertType.class))).then(invocation -> null);
            String message = "error";
            String title = ResourceManager.get("error.title");
            String expectedContent = ResourceManager.get("error.generic") + "\nerror";
            String header = ResourceManager.get("error.header");
            String confirm = ResourceManager.get("button.confirm");

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            sceneManager.showError(message);

            verify(soundService).popup();
            mockedHelper.verify(() -> PresenterAndStageHelper
                    .showAlert(title, expectedContent, header, confirm, Alert.AlertType.ERROR));
        }
    }

    @Test
    void showLoadingLobbyWindow() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .makeAndShowLoadingLobbyWindow(isA(LobbyName.class), isA(LobbyStageMap.class)))
                        .then(invocation -> null);

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            sceneManager.showLoadingLobbyWindow(defaultLobby);

            mockedHelper.verify(() -> PresenterAndStageHelper
                    .makeAndShowLoadingLobbyWindow(eq(defaultLobby), isA(LobbyStageMap.class)));
        }
    }

    @Test
    void showLobbyWindow() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .makeAndShowStage(isA(Stage.class), isA(String.class), isA(String.class), isA(Integer.class),
                                      isA(Integer.class), isA(Double.class), isA(Double.class), isA(LobbyName.class),
                                      isA(LobbyStageMap.class), isA(EventHandler.class), isA(Boolean.class),
                                      isA(Boolean.class), isA(CountDownLatch.class))).then(invocation -> null);
            doReturn(900.0).when(primary).getX();
            String fxml = LobbyPresenter.fxml;
            String title = defaultLobby.toString();
            int minHeight = LobbyPresenter.MIN_HEIGHT_PRE_GAME;
            int minWidth = LobbyPresenter.MIN_WIDTH_PRE_GAME;
            double expectedXPos = 900.0 - 0.5 * LobbyPresenter.MIN_WIDTH_IN_GAME;

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            sceneManager.showLobbyWindow(defaultLobby, mockLatch);

            mockedHelper.verify(() -> PresenterAndStageHelper
                    .makeAndShowStage(eq(primary), eq(fxml), eq(title), eq(minHeight), eq(minWidth), eq(expectedXPos),
                                      eq(10.0), eq(defaultLobby), isA(LobbyStageMap.class), isA(EventHandler.class),
                                      eq(false), eq(true), eq(mockLatch)));
        }
    }

    @Test
    void showLobbyWindow_PrimaryTooFarLeft() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .makeAndShowStage(isA(Stage.class), isA(String.class), isA(String.class), isA(Integer.class),
                                      isA(Integer.class), isA(Double.class), isA(Double.class), isA(LobbyName.class),
                                      isA(LobbyStageMap.class), isA(EventHandler.class), isA(Boolean.class),
                                      isA(Boolean.class), isA(CountDownLatch.class))).then(invocation -> null);
            doReturn(5.0).when(primary).getX();
            String fxml = LobbyPresenter.fxml;
            String title = defaultLobby.toString();
            int minHeight = LobbyPresenter.MIN_HEIGHT_PRE_GAME;
            int minWidth = LobbyPresenter.MIN_WIDTH_PRE_GAME;

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            sceneManager.showLobbyWindow(defaultLobby, mockLatch);

            mockedHelper.verify(() -> PresenterAndStageHelper
                    .makeAndShowStage(eq(primary), eq(fxml), eq(title), eq(minHeight), eq(minWidth), eq(10.0), eq(10.0),
                                      eq(defaultLobby), isA(LobbyStageMap.class), isA(EventHandler.class), eq(false),
                                      eq(true), eq(mockLatch)));
        }
    }

    @Test
    void showLogOldSessionOutScreen() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .showAndGetConfirmation(isA(String.class), isA(String.class), isA(String.class), isA(String.class),
                                            isA(String.class), isA(Alert.AlertType.class), isA(Runnable.class)))
                        .then(invocation -> null);
            String title = ResourceManager.get("confirmation.title");
            String expectedContent = ResourceManager.get("logoldsessionout.error");
            String header = ResourceManager.get("confirmation.header");
            String confirm = ResourceManager.get("button.confirm");
            String cancel = ResourceManager.get("button.cancel");

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            sceneManager.showLogOldSessionOutScreen(mockUser);

            verify(soundService).popup();
            mockedHelper.verify(() -> PresenterAndStageHelper
                    .showAndGetConfirmation(eq(title), eq(expectedContent), eq(header), eq(confirm), eq(cancel),
                                            eq(Alert.AlertType.CONFIRMATION), isA(Runnable.class)));
        }
    }

    @Test
    void showLoginScreen() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .showSceneOnPrimaryStage(isA(Stage.class), isA(Scene.class), isA(String.class), isA(Integer.class),
                                             isA(Integer.class))).then(invocation -> null);
            String title = ResourceManager.get("login.window.title");
            int minHeight = LoginPresenter.MIN_HEIGHT;
            int minWidth = LoginPresenter.MIN_WIDTH;

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            sceneManager.showLoginScreen();

            mockedHelper.verify(() -> PresenterAndStageHelper
                    .showSceneOnPrimaryStage(eq(primary), isA(Scene.class), eq(title), eq(minWidth), eq(minHeight)));
            verify(primary).setOnCloseRequest(isA(EventHandler.class));
        }
    }

    @Test
    void showMainScreen() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .showSceneOnPrimaryStage(isA(Stage.class), isA(Scene.class), isA(String.class), isA(Integer.class),
                                             isA(Integer.class))).then(invocation -> null);
            String title = ResourceManager.get("mainmenu.window.title", mockUser);
            int minHeight = MainMenuPresenter.MIN_HEIGHT;
            int minWidth = MainMenuPresenter.MIN_WIDTH;

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            sceneManager.showMainScreen(mockUser);

            mockedHelper.verify(() -> PresenterAndStageHelper
                    .showSceneOnPrimaryStage(eq(primary), isA(Scene.class), eq(title), eq(minWidth), eq(minHeight)));
            verify(primary).setOnCloseRequest(isA(EventHandler.class));
        }
    }

    @Test
    void showRegistrationScreen() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .showSceneOnPrimaryStage(isA(Stage.class), isA(Scene.class), isA(String.class), isA(Integer.class),
                                             isA(Integer.class))).then(invocation -> null);
            String title = ResourceManager.get("register.window.title");
            int minHeight = RegistrationPresenter.MIN_HEIGHT;
            int minWidth = RegistrationPresenter.MIN_WIDTH;

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            sceneManager.showRegistrationScreen();

            mockedHelper.verify(() -> PresenterAndStageHelper
                    .showSceneOnPrimaryStage(eq(primary), isA(Scene.class), eq(title), eq(minWidth), eq(minHeight)));
        }
    }

    @Test
    void showRobberTaxWindow() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .makeAndShowStage(isA(Stage.class), isA(String.class), isA(String.class), isA(Integer.class),
                                      isA(Integer.class), isA(LobbyName.class), isA(LobbyStageMap.class),
                                      isA(EventHandler.class), isA(Boolean.class), isA(CountDownLatch.class)))
                        .then(invocation -> null);
            String title = ResourceManager.get("game.robber.tax.title");
            String fxml = RobberTaxPresenter.fxml;
            int minHeight = RobberTaxPresenter.MIN_HEIGHT;
            int minWidth = RobberTaxPresenter.MIN_WIDTH;

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            sceneManager.showRobberTaxWindow(defaultLobby, mockLatch);

            mockedHelper.verify(() -> PresenterAndStageHelper
                    .makeAndShowStage(eq(primary), eq(fxml), eq(title), eq(minHeight), eq(minWidth), eq(defaultLobby),
                                      isA(LobbyStageMap.class), isA(EventHandler.class), eq(true), eq(mockLatch)));
        }
    }

    @Test
    void showRulesWindow() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .showStageFromScene(isA(Stage.class), isA(String.class), isA(Integer.class), isA(Integer.class),
                                        isA(Scene.class), isA(EventHandler.class))).then(invocation -> null);
            String title = ResourceManager.get("rules.window.title");
            int minHeight = RulesOverviewPresenter.MIN_HEIGHT;
            int minWidth = RulesOverviewPresenter.MIN_WIDTH;

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            sceneManager.showRulesWindow();

            mockedHelper.verify(() -> PresenterAndStageHelper
                    .showStageFromScene(eq(primary), eq(title), eq(minHeight), eq(minWidth), isA(Scene.class),
                                        isA(EventHandler.class)));
        }
    }

    @Test
    void showTimeoutErrorScreen() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .showAlert(isA(String.class), isA(String.class), isA(String.class), isA(String.class),
                               isA(Alert.AlertType.class))).then(invocation -> null);
            String title = ResourceManager.get("error.generic");
            String content = ResourceManager.get("error.context.disconnected");
            String header = ResourceManager.get("error.header.disconnected");
            String confirm = ResourceManager.get("button.confirm");

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            sceneManager.showTimeoutErrorScreen();

            verify(soundService).popup();
            mockedHelper.verify(() -> PresenterAndStageHelper
                    .showAlert(title, content, header, confirm, Alert.AlertType.ERROR));
        }
    }

    @Test
    void showUserTradeWindow() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .makeAndShowStage(isA(Stage.class), isA(String.class), isA(String.class), isA(Integer.class),
                                      isA(Integer.class), isA(LobbyName.class), isA(LobbyStageMap.class),
                                      isA(EventHandler.class), isA(Boolean.class), isA(CountDownLatch.class)))
                        .then(invocation -> null);
            String title = ResourceManager.get("game.trade.window.offering.title", mockActor);
            String fxml = TradeWithUserPresenter.fxml;
            int minHeight = TradeWithUserPresenter.MIN_HEIGHT;
            int minWidth = TradeWithUserPresenter.MIN_WIDTH;

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            sceneManager.showUserTradeWindow(defaultLobby, mockActor, mockLatch);

            mockedHelper.verify(() -> PresenterAndStageHelper
                    .makeAndShowStage(eq(primary), eq(fxml), eq(title), eq(minHeight), eq(minWidth), eq(defaultLobby),
                                      isA(LobbyStageMap.class), isA(EventHandler.class), eq(false), eq(mockLatch)));
        }
    }

    @Test
    void testShowError() {
        try (MockedStatic<PresenterAndStageHelper> mockedHelper = mockStatic(PresenterAndStageHelper.class)) {
            mockedHelper.when(() -> PresenterAndStageHelper.initPresenter(isA(String.class)))
                        .thenReturn(new Scene(new Pane()));
            mockedHelper.when(() -> PresenterAndStageHelper
                    .showAlert(isA(String.class), isA(String.class), isA(String.class), isA(String.class),
                               isA(Alert.AlertType.class))).then(invocation -> null);
            String message = "error";
            String title = ResourceManager.get("error.title");
            String header = ResourceManager.get("error.header");
            String confirm = ResourceManager.get("button.confirm");
            String expectedContent = "super\nerror";

            SceneManager sceneManager = new SceneManager(soundService, eventBus, primary, tradeService);

            sceneManager.showError("super\n", message);

            verify(soundService).popup();
            mockedHelper.verify(() -> PresenterAndStageHelper
                    .showAlert(title, expectedContent, header, confirm, Alert.AlertType.ERROR));
        }
    }
}