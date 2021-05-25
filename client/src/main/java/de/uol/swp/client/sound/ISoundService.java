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
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void background();

    /**
     * Plays the building sound.
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void building();

    /**
     * Plays the button sound.
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void button();

    /**
     * Plays the coins sound.
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void coins();

    /**
     * Plays the dice sound.
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void dice();

    /**
     * Plays the popup sound.
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void popup();

    /**
     * Plays the victory sound.
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void victory();
}
