package de.uol.swp.common.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private final List<String> aiNameEasy = new ArrayList<>(
            Arrays.asList("Jonathan Joestar", "Robert E. O. Speedwagon", "Erina Pendleton", "George Joestar I.",
                          "Lisa Lisa", "Johnny Joestar", //British
                          "Rudol von Stroheim", //German
                          "Jean Pierre Polnareff", //French
                          "Will A. Zeppeli", "Caesar Zeppeli", "Suzie Q", "Gyro Zeppeli", //English with Italian accent
                          "Tonpetty", "Dire", "Straizo", //English with Tibetan accent
                          "Jotaro Kujo", "Noriaki Kakyoin", //English with Japanese accent
                          "Muhammad Avdol", //Arabic
                          "Joseph Joestar", "Smokey Brown", "Holy Kujo", "Jolyne Cujoh", "Ermes Costello",
                          "Emporio Alnino", "Weather Report", "Foo Fighters", "Lucy Steel", "Hot Pants", //US-American
                          "Iggy", "Coco Jumbo", "Danny", //Animal
                          "Josuke Higashikata", "Koichi Hirose", "Okuyasu Nijimura", "Rohan Kishibe", "Shigechi",
                          "Yasuho Hirose", "Rai Mamezuku", //Japanese
                          "Giorno Giovanna", "Bruno Bucciarati", "Leone Abbacchio", "Guido Mista", "Narancia Ghirga",
                          "Pannacotta Fugo", "Trish Una", "Tonio Trussardi")); //Italian
    private final List<String> aiNameHard = new ArrayList<>(
            Arrays.asList("Dio Brando", "Bruford", "Tarkus", "Jack the Ripper", "DIO", "Diego Brando", //British
                          "Kars", "Esidisi", "Wamuu", "Santana", //Aztec
                          "Enya the Hag", "Oingo", "Boingo", "Steely Dan", "Kenny G.", "J. Geil", "Vanilla Ice",
                          //Arabic
                          "Hol Horse", "Terence T. D'Arby", "Daniel J. D'Arby", "Axl Ro", "Enrico Pucci",
                          //US-American
                          "Pet Shop", "Bug-Eaten", "Not Bug-Eaten", "Stray Cat", //Animals
                          "Yoshikage Kira", "Head Doctor", // Japanese
                          "Diavolo", "Polpo", "Doppio")); //Italian
    private final List<String> aiNameEasyNoTalk = new ArrayList<>(
            Arrays.asList("Star Platinum", "Stone Free", "Soft & Wet", "Tusk", "Silver Chariot", "Crazy Diamond",
                          "Gold Experience", "Sticky Fingers", "Moody Blues", "Sex Pistols", "Aerosmith", "Purple Haze",
                          "Spice Girl", "Kiss", "Magician's Red", "Hermit Purple", "Hierophant Green", "The Fool",
                          "Earth, Wind and Fire", "The Hand", "Echoes", "Heaven's Door", "Love Deluxe", "Achtung Baby!",
                          "Mr. President", "Burning Down The House", "Ball Breaker", "Oh! Lonesome Me", "Cream Starter",
                          "Ticket to Ride", "Paisley Park", "Doggy Style", "Nut King Call", "Paper Moon King",
                          "King Nothing"));
    private final List<String> aiNameHardNoTalk = new ArrayList<>(
            Arrays.asList("The World", "Killer Queen", "Metallica", "Oasis", "Whitesnake", "C-Moon", "Scary Monsters",
                          "Speed King", "Bad Company", "Red Hot Chilli Pepper", "Atom Heart Father", "Highway Star",
                          "Super Fly", "Enigma", "Cheap Trick", "Sheer Heart Attack", "Bites the Dust", "Black Sabbath",
                          "Beach Boy", "Man in the Mirror", "Kraft Work", "The Grateful Dead", "Notorious B.I.G",
                          "Green Day", "Rolling Stones", "King Crimson", "Talking Head", "Made in Heaven",
                          "Highway to Hell", "Limp Bizkit", "Bohemian Rhapsody", "Driver Down", "Jumpin' Jack Flash",
                          "Jail House Lock", "Yo-Yo Ma", "Survivor", "Dirty Deeds Done Dirt Cheap", "Hey Ya!",
                          "Tomb of the Boom", "Catch the Rainbow", "Civil War", "In a Silent Way", "Fun Fun Fun",
                          "I Am a Rock", "Doobie Wah!", "Vitamin C", "Milagro Man", "Ozone Baby", "Wonder of U"));
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
        String name = "Man X";
        switch (difficulty) {
            case EASY:
                name = aiTalking ? aiNameEasy.get((int) (Math.random() * aiNameEasy.size())) :
                       aiNameEasyNoTalk.get((int) (Math.random() * aiNameEasyNoTalk.size()));
                break;
            case HARD:
                name = aiTalking ? aiNameHard.get((int) (Math.random() * aiNameHard.size())) :
                       aiNameHardNoTalk.get((int) (Math.random() * aiNameHardNoTalk.size()));
                if ((int) (Math.random() * 10000) <= 1) name = "Temmo";
                break;
        }
        this.name = name;
        setLanguage();
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
        this.name = name;
        difficulty = aiNameHardNoTalk.contains(name) || aiNameHard.contains(name) ? Difficulty.HARD : Difficulty.EASY;
    }

    @Override
    public int compareTo(UserOrDummy o) {
        if (o instanceof AI) return name.compareTo(o.getUsername());
        else return 1;
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

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public List<String> getAINames() {
        List<String> names = new ArrayList<>();
        names.addAll(aiNameEasy);
        names.addAll(aiNameEasyNoTalk);
        names.addAll(aiNameHard);
        names.addAll(aiNameHardNoTalk);
        return names;
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    @Override
    public Difficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public void setAiTalking(boolean b) {
        aiTalking = b;
    }

    /**
     * Sets the AI's language
     *
     * @since 2021-05-20
     */
    private void setLanguage() {
        Language language = Language.NONE;
        if (aiNameEasy.subList(0, 6).contains(name)) language = Language.BRITISH;
        else if (aiNameEasy.subList(9, 19).contains(name)) language = Language.SIMPLE_ENGLISH;
        else if (aiNameEasy.subList(18, 28).contains(name)) language = Language.US_AMERICAN;
        else if (aiNameEasy.subList(31, 38).contains(name)) language = Language.JAPANESE;
        else if (aiNameEasy.subList(38, aiNameEasy.size()).contains(name)) language = Language.ITALIAN;
        else if (aiNameHard.subList(0, 6).contains(name)) language = Language.BRITISH;
        else if (aiNameHard.subList(6, 10).contains(name)) language = Language.AZTEC;
        else if (aiNameHard.subList(10, 17).contains(name)) language = Language.ARABIC;
        else if (aiNameHard.subList(17, 23).contains(name)) language = Language.US_AMERICAN;

        this.language = language;
    }
}
