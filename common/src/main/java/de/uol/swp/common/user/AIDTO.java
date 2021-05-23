package de.uol.swp.common.user;

import java.util.List;
import java.util.Objects;

/**
 * A class for AI users
 *
 * @author Mario Fokken
 * @since 2021-05-11
 */
public class AIDTO implements AI {

    private static int idCounter = 0;
    private final int id;
    private final String name;
    private final Difficulty difficulty;
    private final de.uol.swp.common.user.AINames aiNames = new AINames(this);
    private boolean aiTalking;
    private Language language;

    /**
     * Constructor
     *
     * @param difficulty The AI's difficulty
     * @param aiTalking  If the AI writes commands
     */
    public AIDTO(Difficulty difficulty, boolean aiTalking) {
        this.aiTalking = aiTalking;
        this.id = ++idCounter;
        this.name = aiNames.getAIName(difficulty, aiTalking);
        this.language = aiNames.getLanguage(name);
        this.difficulty = difficulty;
    }

    /**
     * Constructor
     *
     * @param difficulty The AI's difficulty
     */
    public AIDTO(Difficulty difficulty) {
        this(difficulty, true);
    }

    /**
     * Constructor with username
     *
     * @param name The AI's name
     */
    public AIDTO(String name) {
        this.id = ++idCounter;
        Difficulty diff = aiNames.getDifficultyFromName(name);
        if (diff == null) {
            this.name = "Man X";
            this.difficulty = Difficulty.EASY;
        } else {
            this.name = name;
            this.difficulty = diff;
        }
    }

    @Override
    public int compareTo(UserOrDummy o) {
        if (o instanceof AI) return name.compareTo(o.getUsername());
        else if (o instanceof User) return 1;
        else return -1;
    }

    @Override
    public List<String> getAiNames() {
        return aiNames.getAINames();
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public String writeMessage(WriteType writeType) {
        return aiNames.writeMessage(name, difficulty, language, writeType);
    }

    @Override
    public Difficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUsername());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UserOrDummy) return compareTo((UserOrDummy) o) == 0;
        return false;
    }

    @Override
    public String toString() {
        return "(AI) " + getUsername();
    }
}
