package de.uol.swp.client.sound;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.scene.media.AudioClip;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Class used to play various sounds
 *
 * @author Marvin Drees
 * @since 2021-05-15
 */
public class SoundService implements ISoundService {

    private static final Logger LOG = LogManager.getLogger(SoundService.class);

    private final String soundPack;
    private final double volume;
    private final double backgroundVolume;

    /**
     * Constructor
     *
     * @param soundPack        The soundPack this class should use.
     * @param volume           The volume this class should use.
     * @param backgroundVolume the backgroundVolume this class should use.
     */
    @Inject
    public SoundService(@Named("soundPack") String soundPack, @Named("volume") double volume,
                        @Named("backgroundVolume") double backgroundVolume) {
        this.soundPack = soundPack;
        this.volume = volume;
        this.backgroundVolume = backgroundVolume;
    }

    @Override
    public void background() {
        AudioClip audio = new AudioClip(new File(soundPack + "background.wav").toURI().toString());
        audio.setCycleCount(10000);
        audio.setPriority(-1);
        audio.play(backgroundVolume);
        LOG.debug("SoundService initialised");
    }

    @Override
    public void building() {
        playSound("building.wav");
    }

    @Override
    public void button() {
        playSound("button.wav");
    }

    @Override
    public void coins() {
        playSound("coins.wav");
    }

    @Override
    public void dice() {
        playSound("dice.wav");
    }

    @Override
    public void popup() {
        playSound("popup.wav");
    }

    @Override
    public void victory() {
        playSound("victory.wav");
    }

    /**
     * Plays a sound file with a specific volume
     *
     * @param sound Name of the sound file to play
     */
    private void playSound(String sound) {
        AudioClip audio = new AudioClip(new File(soundPack + sound).toURI().toString());
        audio.play(volume);
    }
}
