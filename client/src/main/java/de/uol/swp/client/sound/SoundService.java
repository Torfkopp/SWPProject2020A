package de.uol.swp.client.sound;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.scene.media.AudioClip;

import java.io.File;

public class SoundService implements ISoundService {

    @Inject
    @Named("soundPack")
    private static String soundPack;

    @Override
    public void buttonSound() {
        AudioClip sound = new AudioClip(new File(soundPack + "sample.wav").toURI().toString());
        sound.play();
    }
}
