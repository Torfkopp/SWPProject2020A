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
            Arrays.asList("Jonathan Joestar", "Will A. Zeppeli", "Robert E. O. Speedwagon", "Erina Pendleton",
                          "Tonpetty", "Dire", "Straizo", "George Joestar I.", "Joseph Joestar", "Caesar Zeppeli",
                          "Lisa Lisa", "Smokey Brown", "Suzie Q", "Rudol von Stroheim", "Jotaro Kujo", "Muhammad Avdol",
                          "Noriaki Kakyoin", "Jean Pierre Polnareff", "Iggy", "Holy Kujo", "Josuke Higashikata",
                          "Koichi Hirose", "Okuyasu Nijimura", "Rohan Kishibe", "Tonio Trussardi", "Shigechi",
                          "Giorno Giovanna", "Bruno Bucciarati", "Leone Abbacchio", "Guido Mista", "Narancia Ghirga",
                          "Pannacotta Fugo", "Trish Una", "Coco Jumbo", "Jolyne Cujoh", "Ermes Costello",
                          "Emporio Alnino", "Foo Fighters", "Weather Report", "Johnny Joestar", "Gyro Zeppeli",
                          "Lucy Steel", "Hot Pants", "Yasuho Hirose", "Rai Mamezuku", "Star Platinum", "Magician's Red",
                          "Hermit Purple", "Hierophant Green", "Silver Chariot", "The Fool", "Crazy Diamond",
                          "Earth, Wind and Fire", "The Hand", "Echoes", "Heaven's Door", "Love Deluxe", "Achtung Baby!",
                          "Gold Experience", "Sticky Fingers", "Moody Blues", "Sex Pistols", "Aerosmith", "Purple Haze",
                          "Spice Girl", "Mr. President", "Stone Free", "Kiss", "Burning Down The House", "Tusk",
                          "Ball Breaker", "Oh! Lonesome Me", "Cream Starter", "Ticket to Ride", "Soft & Wet",
                          "Paisley Park", "Doggy Style", "Nut King Call", "Paper Moon King", "King Nothing"));
    private final List<String> aiNameHard = new ArrayList<>(
            Arrays.asList("Dio Brando", "Bruford", "Tarkus", "Jack the Ripper", "Kars", "Esidisi", "Wamuu", "Santana",
                          "DIO", "Enya the Hag", "Vanilla Ice", "Hol Horse", "Pet Shop", "Terence T. D'Arby",
                          "Daniel J. D'Arby", "Oingo", "Boingo", "Steely Dan", "Kenny G.", "J. Geil", "Yoshikage Kira",
                          "Diavolo", "Polpo", "Enrico Pucci", "Diego Brando", "Sandman", "Axl Ro", "The World",
                          "Bug-Eaten", "Not Bug-Eaten", "Bad Company", "Red Hot Chilli Pepper", "Atom Heart Father",
                          "Highway Star", "Super Fly", "Stray Cat", "Enigma", "Cheap Trick", "Killer Queen",
                          "Sheer Heart Attack", "Bites the Dust", "Black Sabbath", "Beach Boy", "Man in the Mirror",
                          "Kraft Work", "The Grateful Dead", "Notorious B.I.G", "Green Day", "Oasis", "Metallica",
                          "Rolling Stones", "King Crimson", "Talking Head", "Whitesnake", "C-Moon", "Made in Heaven",
                          "Highway to Hell", "Limp Bizkit", "Bohemian Rhapsody", "Driver Down", "Jumpin' Jack Flash",
                          "Jail House Lock", "Yo-Yo Ma", "Manhatten Transfer", "Survivor", "Scary Monsters",
                          "Dirty Deeds Done Dirt Cheap", "Hey Ya!", "Tomb of the Boom", "Catch the Rainbow",
                          "Civil War", "In a Silent Way", "Speed King", "Fun Fun Fun", "I Am a Rock", "Doobie Wah!",
                          "Vitamin C", "Milagro Man", "Ozone Baby", "Wonder of U"));

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
                name = aiNameEasy.get((int) (Math.random() * aiNameEasy.size()));
                break;
            case HARD:
                name = aiNameHard.get((int) (Math.random() * aiNameHard.size()));
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

    public List<String> getAINames() {
        List<String> list = new ArrayList<>(aiNameEasy);
        list.addAll(aiNameHard);
        return list;
    }

    @Override
    public Difficulty getDifficulty() {
        return difficulty;
    }
}
