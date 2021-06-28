package de.uol.swp.client.scene;

import com.google.common.eventbus.EventBus;
import de.uol.swp.client.scene.util.PresenterAndStageHelper;
import de.uol.swp.client.sound.ISoundService;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@SuppressWarnings("UnstableApiUsage")
class SceneManagerProviderTest {

    @Test
    void provideSceneManager() {
        try (MockedStatic<PresenterAndStageHelper> helper = mockStatic(PresenterAndStageHelper.class)) {
            ISoundService soundService = mock(ISoundService.class);
            EventBus eventBus = mock(EventBus.class);
            Stage stage = mock(Stage.class);
            doNothing().when(eventBus).register(isA(SceneManager.class));
            doNothing().when(eventBus).post(isA(Object.class));
            helper.when(() -> PresenterAndStageHelper.initPresenter(anyString())).thenAnswer(Answers.RETURNS_DEFAULTS);

            SceneManagerProvider provider = new SceneManagerProvider();

            SceneManager sceneManager = provider.provideSceneManager(soundService, eventBus, stage);

            SceneManager sceneManager2 = provider.provideSceneManager(soundService, eventBus, stage);

            assertSame(sceneManager, sceneManager2);
        }
    }
}