package de.uol.swp.client.sound;

/**
 * Interface for the SoundService class
 *
 * @author Marvin Drees
 * @since 2021-05-15
 */
public interface ISoundService {

    /**
     * Plays background music.
     */
    void background();

    /**
     * Plays the building sound.
     */
    void building();

    /**
     * Plays the button sound.
     */
    void button();

    /**
     * Plays the coins sound.
     */
    void coins();

    /**
     * Plays the dice sound.
     */
    void dice();

    /**
     * Plays the popup sound.
     */
    void popup();

    /**
     * Plays the victory sound.
     */
    void victory();
}
