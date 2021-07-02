package de.uol.swp.client.scene;

import com.google.common.eventbus.EventBus;
import com.google.inject.Provides;
import com.google.inject.assistedinject.Assisted;
import de.uol.swp.client.sound.ISoundService;
import de.uol.swp.client.trade.ITradeService;
import javafx.stage.Stage;

/**
 * Provider for the SceneManager class used by Guice when a SceneManager
 * needs to be injected.
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.di.ClientModule
 * @see com.google.inject.Provides
 * @since 2021-06-24
 */
@SuppressWarnings("UnstableApiUsage")
public class SceneManagerProvider {

    private SceneManager sceneManager;

    /**
     * Method to provide an instance of the SceneManager
     *
     * @param soundService The ISoundService to be used (injected)
     * @param eventBus     The EventBus to be used (injected)
     * @param primaryStage The primary Stage created by JavaFX (assisted injected)
     * @param tradeService The ITradeService to be used (injected)
     *
     * @return An instance of the SceneManager
     *
     * @implNote This method employs the Singleton pattern and as such only
     * provides one instance to Guice.
     */
    @Provides
    public SceneManager provideSceneManager(ISoundService soundService, EventBus eventBus, @Assisted Stage primaryStage,
                                            ITradeService tradeService) {
        if (sceneManager == null) {
            this.sceneManager = new SceneManager(soundService, eventBus, primaryStage, tradeService);
        }
        return this.sceneManager;
    }
}
