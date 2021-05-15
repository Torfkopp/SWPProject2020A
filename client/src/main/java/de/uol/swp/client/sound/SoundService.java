package de.uol.swp.client.sound;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.scene.media.AudioClip;

import java.io.File;

/**
 * Class used to play various sounds
 *
 * @author Marvin Drees
 * @since 2021-05-15
 */
public class SoundService implements ISoundService {

    @Inject
    @Named("soundPack")
    private static String soundPack;

    @Inject
    @Named("volume")
    private static double volume;

    @Override
    public void building() {
        playSound("hammer.wav");
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
    public void fanfare() {
        playSound("fanfare.wav");
    }

    @Override
    public void notification() {
        playSound("notification.wav");
    }

    @Override
    public void popup() {
        playSound("popup.wav");
    }

    /**
     * Plays a sound file with a specific volume
     *
     * @param sound Name of the sound file to play
     */
    private void playSound(String sound) {
        AudioClip audio = new AudioClip(new File(soundPack + sound).toURI().toString());
        audio.setVolume(volume);
        audio.play();
    }
}
