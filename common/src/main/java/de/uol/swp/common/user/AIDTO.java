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
                          "Iggy", "Coco Jumbo", //Animal
                          "Josuke Higashikata", "Koichi Hirose", "Okuyasu Nijimura", "Rohan Kishibe", "Shigechi",
                          "Yasuho Hirose", "Rai Mamezuku", //Japanese
                          "Giorno Giovanna", "Bruno Bucciarati", "Leone Abbacchio", "Guido Mista", "Narancia Ghirga",
                          "Pannacotta Fugo", "Trish Una", "Tonio Trussardi")); //Italian
    private final List<String> aiNameHard = new ArrayList<>(
            Arrays.asList("Dio Brando", "Bruford", "Tarkus", "Jack the Ripper", "DIO", "Diego Brando", //British
                          "Kars", "Esidisi", "Wamuu", "Santana", //Aztec
                          "Enya the Hag", "Oingo", "Boingo", "Steely Dan", "Kenny G.", "J. Geil", "Vanilla Ice",
                          //Arabic
                          "Hol Horse", "Terence T. D'Arby", "Daniel J. D'Arby", "Axl Ro", "Enrico Pucci", "Sandman",
                          //US-American
                          "Pet Shop", "Bug-Eaten", "Not Bug-Eaten", "Stray Cat", //Animals
                          "Yoshikage Kira", "Head Doctor", // Japanese
                          "Diavolo", "Polpo")); //Italian
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
    private boolean aiTalking = true;

    /**
     * Constructor
     *
     * @param difficulty The AI's difficulty
     */
    public AIDTO(Difficulty difficulty) {
        this.id = ++idCounter;
        String name = "Danny";
        switch (difficulty) {
            case EASY:
                name = aiTalking ? aiNameEasy.get((int) (Math.random() * aiNameEasy.size())) :
                       aiNameEasyNoTalk.get((int) (Math.random() * aiNameEasyNoTalk.size()));
                break;
            case HARD:
                name = aiTalking ? aiNameHard.get((int) (Math.random() * aiNameHard.size())) :
                       aiNameHardNoTalk.get((int) (Math.random() * aiNameHardNoTalk.size()));
                break;
        }
        this.name = name;
        this.difficulty = difficulty;
    }

    /**
     * Constructor with username
     *
     * @param difficulty The AI's difficulty
     * @param name       The AI's name
     */
    public AIDTO(Difficulty difficulty, String name) {
        this.id = ++idCounter;
        this.name = name;
        this.difficulty = difficulty;
    }

    @Override
    public int compareTo(UserOrDummy o) {
        Integer id_obj = id; // compareTo is only defined on the wrapper class, so we make one here
        if (o instanceof AI) return id_obj.compareTo(o.getID());
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
    public List<String> getAINameEasy() {
        return aiNameEasy;
    }

    @Override
    public List<String> getAINameHard() {
        return aiNameHard;
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
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Sets if the AI is allowed
     * to write chat messages
     *
     * @param b Boolean
     */
    public void setAiTalking(boolean b) {
        aiTalking = b;
    }
}
