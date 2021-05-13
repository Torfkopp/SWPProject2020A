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
     * Gets all possible AI names
     *
     * @return AI names
     */
    List<String> getAINames();

    /**
     * Gets the difficulty
     *
     * @return AI's difficulty
     */
    Difficulty getDifficulty();
}
