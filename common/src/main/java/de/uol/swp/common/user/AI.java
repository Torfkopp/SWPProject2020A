package de.uol.swp.common.user;

import java.io.Serializable;
import java.util.List;

/**
 * An interface for AI users
 *
 * @author Mario Fokken
 * @since 2021-05-11
 */
public interface AI extends NPC, Serializable {

    /**
     * Enum for an AI's difficulty
     */
    enum Difficulty {
        EASY,
        HARD
    }

    /**
     * Enum for an AI's language
     *
     * @since 2021-05-20
     */
    enum Language {
        BRITISH,
        US_AMERICAN,
        JAPANESE,
        ITALIAN,
        ARABIC,
        AZTEC,
        SIMPLE_ENGLISH,
        NONE
    }

    /**
     * Gets all possible AI names
     *
     * @return List of Strings
     */
    List<String> getAINames();

    /**
     * Gets the AI's language
     *
     * @return Language
     */
    Language getLanguage();

    /**
     * Gets the difficulty
     *
     * @return AI's difficulty
     */
    Difficulty getDifficulty();

    /**
     * Sets if the AI is able
     * to write chat messages
     *
     * @param b Boolean
     */
    void setAiTalking(boolean b);
}
