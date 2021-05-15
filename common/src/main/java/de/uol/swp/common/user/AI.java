package de.uol.swp.common.user;

import java.io.Serializable;
import java.util.List;

/**
 * An interface for AI users
 *
 * @author Mario Fokken
 * @since 2021-05-11
 */
public interface AI extends ComputedPlayer, Serializable {

    enum Difficulty {
        EASY,
        HARD
    }

    /**
     * Gets all AI names for
     * difficulty easy and
     * activated talking
     *
     * @return List of Strings
     */
    List<String> getAINameEasy();

    /**
     * Gets all AI names for
     * difficulty hard and
     * activated talking
     *
     * @return List of Strings
     */
    List<String> getAINameHard();

    /**
     * Gets all possible AI names
     *
     * @return List of Strings
     */
    List<String> getAINames();

    /**
     * Gets the difficulty
     *
     * @return AI's difficulty
     */
    Difficulty getDifficulty();
}
