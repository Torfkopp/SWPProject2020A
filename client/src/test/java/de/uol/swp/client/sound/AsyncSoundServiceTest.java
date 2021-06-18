package de.uol.swp.client.sound;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AsyncSoundServiceTest {

    private static final long DURATION = 200L;
    private final SoundService syncSoundService = mock(SoundService.class);
    private AsyncSoundService soundService;

    @BeforeEach
    protected void setUp() {
        assertNotNull(syncSoundService);
        soundService = new AsyncSoundService(syncSoundService);
    }

    @AfterEach
    protected void tearDown() {
        soundService = null;
    }

    @Test
    void background() {
        doNothing().when(syncSoundService).background();

        soundService.background();

        verify(syncSoundService, after(DURATION)).background();
    }

    @Test
    void building() {
        doNothing().when(syncSoundService).building();

        soundService.building();

        verify(syncSoundService, after(DURATION)).building();
    }

    @Test
    void button() {
        doNothing().when(syncSoundService).button();

        soundService.button();

        verify(syncSoundService, after(DURATION)).button();
    }

    @Test
    void coins() {
        doNothing().when(syncSoundService).coins();

        soundService.coins();

        verify(syncSoundService, after(DURATION)).coins();
    }

    @Test
    void dice() {
        doNothing().when(syncSoundService).dice();

        soundService.dice();

        verify(syncSoundService, after(DURATION)).dice();
    }

    @Test
    void popup() {
        doNothing().when(syncSoundService).popup();

        soundService.popup();

        verify(syncSoundService, after(DURATION)).popup();
    }

    @Test
    void victory() {
        doNothing().when(syncSoundService).victory();

        soundService.victory();

        verify(syncSoundService, after(DURATION)).victory();
    }
}