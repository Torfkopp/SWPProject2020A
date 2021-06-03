package de.uol.swp.client.sound;

import com.google.inject.Inject;
import de.uol.swp.client.util.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An asynchronous wrapper for the ISoundService implementation
 * <p>
 * This class handles putting calls to an injected ChatService into
 * their own Task-Thread which is then executed away from the JavaFX
 * Application Thread, isolating non-UI calls onto their own threads.
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.sound.ISoundService
 * @since 2021-05-23
 */
public class AsyncSoundService implements ISoundService {

    private static final Logger LOG = LogManager.getLogger(AsyncSoundService.class);
    private final SoundService syncSoundService;

    @Inject
    public AsyncSoundService(SoundService syncSoundService) {
        this.syncSoundService = syncSoundService;
        LOG.debug("AsyncSoundService initialised");
    }

    @Override
    public void background() {
        ThreadManager.runNow(syncSoundService::background);
    }

    @Override
    public void building() {
        ThreadManager.runNow(syncSoundService::building);
    }

    @Override
    public void button() {
        ThreadManager.runNow(syncSoundService::button);
    }

    @Override
    public void coins() {
        ThreadManager.runNow(syncSoundService::coins);
    }

    @Override
    public void dice() {
        ThreadManager.runNow(syncSoundService::dice);
    }

    @Override
    public void popup() {
        ThreadManager.runNow(syncSoundService::popup);
    }

    @Override
    public void victory() {
        ThreadManager.runNow(syncSoundService::victory);
    }
}
