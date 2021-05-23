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
        GERMAN,
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
     * Enum for all types of
     * ChatMessages an AI can write
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    enum WriteType {
        //Message, when...
        START, //the game starts
        TRADE_ACCEPT, //AI accepts a trade
        TRADE_DECLINE, //AI declines a trade
        GAME_WIN, //AI wins the game
        GAME_LOSE, //AI loses
        MOVE_ROBBER, //AI moves robber
        TAX, //AI has to pay tax
        MONOPOLY, //AI plays a monopoly card
    }

    /**
     * Gets all possible AI names
     *
     * @return List of Strings
     */
    List<String> getAiNames();

    /**
     * Gets a message for an AI
     *
     * @return String
     */
    String writeMessage(WriteType writeType);

    /**
     * Gets the difficulty
     *
     * @return AI's difficulty
     */
    Difficulty getDifficulty();
}
