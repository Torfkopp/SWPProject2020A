package de.uol.swp.common.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for the AI Names
 *
 * @author Mario Fokken
 * @since 2021-05-23
 */
public class AINames implements Serializable {

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

    final static List<String> aiNameEasy = new ArrayList<>(
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
                          "Pannacotta Fugo", "Trish Una",
                          "Tonio Trussardi")); //Italianprivate final java.util.List<java.lang.String> aiNameHard = new java.util.ArrayList<java.lang.String>(
    final static List<String> aiNameHard = new ArrayList<>(
            Arrays.asList("Dio Brando", "Bruford", "Tarkus", "Jack the Ripper", "DIO", "Diego Brando", //British
                          "Kars", "Esidisi", "Wamuu", "Santana", //Aztec
                          "Enya the Hag", "Oingo", "Boingo", "Steely Dan", "Kenny G.", "J. Geil", "Vanilla Ice",
                          //Arabic
                          "Hol Horse", "Terence T. D'Arby", "Daniel J. D'Arby", "Axl Ro", "Enrico Pucci",
                          //US-American
                          "Pet Shop", "Bug-Eaten", "Not Bug-Eaten", "Stray Cat", //Animals
                          "Yoshikage Kira", "Head Doctor", // Japanese
                          "Diavolo", "Polpo", "Doppio")); //Italian
    final static List<String> aiNameEasyNoTalk = new ArrayList<>(
            Arrays.asList("Star Platinum", "Stone Free", "Soft & Wet", "Tusk", "Silver Chariot", "Crazy Diamond",
                          "Gold Experience", "Sticky Fingers", "Moody Blues", "Sex Pistols", "Aerosmith", "Purple Haze",
                          "Spice Girl", "Kiss", "Magician's Red", "Hermit Purple", "Hierophant Green", "The Fool",
                          "Earth, Wind and Fire", "The Hand", "Echoes", "Heaven's Door", "Love Deluxe", "Achtung Baby!",
                          "Mr. President", "Burning Down The House", "Ball Breaker", "Oh! Lonesome Me", "Cream Starter",
                          "Ticket to Ride", "Paisley Park", "Doggy Style", "Nut King Call", "Paper Moon King",
                          "King Nothing"));
    final static List<String> aiNameHardNoTalk = new ArrayList<>(
            Arrays.asList("The World", "Killer Queen", "Metallica", "Oasis", "Whitesnake", "C-Moon", "Scary Monsters",
                          "Speed King", "Bad Company", "Red Hot Chilli Pepper", "Atom Heart Father", "Highway Star",
                          "Super Fly", "Enigma", "Cheap Trick", "Sheer Heart Attack", "Bites the Dust", "Black Sabbath",
                          "Beach Boy", "Man in the Mirror", "Kraft Work", "The Grateful Dead", "Notorious B.I.G",
                          "Green Day", "Rolling Stones", "King Crimson", "Talking Head", "Made in Heaven",
                          "Highway to Hell", "Limp Bizkit", "Bohemian Rhapsody", "Driver Down", "Jumpin' Jack Flash",
                          "Jail House Lock", "Yo-Yo Ma", "Survivor", "Dirty Deeds Done Dirt Cheap", "Hey Ya!",
                          "Tomb of the Boom", "Catch the Rainbow", "Civil War", "In a Silent Way", "Fun Fun Fun",
                          "I Am a Rock", "Doobie Wah!", "Vitamin C", "Milagro Man", "Ozone Baby", "Wonder of U"));

    /**
     * Gets an AI name
     *
     * @param difficulty The AI's difficulty
     * @param aiTalking  If the AI is talking
     *
     * @return String An AI name
     */
    static String getAIName(AI.Difficulty difficulty, Boolean aiTalking) {
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
        return name;
    }

    /**
     * Gets all AI names
     *
     * @return List of Strings
     */
    static List<String> getAINames() {
        List<String> names = new ArrayList<>();
        names.addAll(aiNameEasy);
        names.addAll(aiNameEasyNoTalk);
        names.addAll(aiNameHard);
        names.addAll(aiNameHardNoTalk);
        return names;
    }

    /**
     * Gets the AIs difficulty
     * depending on its name
     *
     * @param name The AI's name
     *
     * @return Difficulty or null if name not found
     */
    static AI.Difficulty getDifficultyFromName(String name) {
        AI.Difficulty diff = null;
        if (aiNameHardNoTalk.contains(name) || aiNameHard.contains(name)) diff = AI.Difficulty.HARD;
        else if (aiNameEasyNoTalk.contains(name) || aiNameEasy.contains(name)) diff = AI.Difficulty.EASY;
        return diff;
    }

    /**
     * Gets the AI's language
     * depending on its name
     *
     * @since 2021-05-20
     */
    static Language getLanguage(String name) {
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

        return language;
    }

    /**
     * Gets an AI's message to write
     * into the chat
     * <p>
     * It looks like shit, but I don't care
     *
     * @param name       The AI's name5
     * @param difficulty The AI's difficulty
     * @param type       The type of message
     *
     * @return String
     */
    static String writeMessage(String name, AI.Difficulty difficulty, AI.WriteType type) {
        String msg = "";
        Language language = getLanguage(name);
        switch (difficulty) {
            case EASY:
                switch (type) {
                    case START:
                        switch (language) {
                            case BRITISH:
                                msg = "Greetings to you, fellow Catan player!";
                                break;
                            case US_AMERICAN:
                                msg = "Howdy, partner!";
                                break;
                            case SIMPLE_ENGLISH:
                                msg = "Hello!";
                                break;
                            case ITALIAN:
                                msg = "Ciao!";
                                break;
                            case JAPANESE:
                                msg = "おはよう おにいちゃん";
                                break;
                        }
                        switch (name) {
                            case "Rudol von Stroheim":
                                msg = "Mein Körper ist das Ergebnis der höchsten Technologie der Deutschen und ich bin stolz darauf. In anderen Worten: Ich stehe über jedem anderen menschlichen Wesen!";
                                break;
                            case "Jean Pierre Polnareff":
                                msg = "Mon Stand est la carte chariot... la machine de guerre, Chariot d'Argent!";
                                break;
                            case "Muhammad Avdol":
                                msg = "آمل ألا تعتقد أن نيران Magician's Red تشتعل فقط للأعلى أو مع الريح أنا لست ملزمًا بقوانين الطبيعة. يمكنني السيطرة على النار كما يحلو لي.";
                                break;
                            case "Iggy":
                                msg = "Woof! (Give me Coffee flavoured gum!)";
                                break;
                            case "Coco Jumbo":
                                msg = "...";
                                break;
                            case "Danny":
                                msg = "Woof!";
                                break;
                            case "Giorno Giovanna":
                                msg = "I, Giorno Giovanna, have a dream!";
                                break;
                            case "":
                                msg = "";
                                break;
                        }
                        break;
                    case TRADE_ACCEPT:
                        switch (language) {
                            case BRITISH:
                                msg = "Your trading offer seems to be profitable for both of us, thus I find this agreeable.";
                                break;
                            case US_AMERICAN:
                                msg = "One hand washes the other, right? *laughs*";
                                break;
                            case SIMPLE_ENGLISH:
                                msg = "It is... acceptable";
                                break;
                            case ITALIAN:
                                msg = "Trovo questa offerta di scambio accettabile";
                                break;
                            case JAPANESE:
                                msg = "承知いたした。";
                                break;
                        }
                        switch (name) {
                            case "Rudol von Stroheim":
                                msg = "Darauf war ich schon vorbereitet, als ich diese Mission annahm!";
                                break;
                            case "Jean Pierre Polnareff":
                                msg = "C'est vrai. Nous devons encore nous présenter. Vous avez eu la gentillesse d'échanger avec Jean Pierre Polnareff.";
                                break;
                            case "Muhammad Avdol":
                                msg = "هل ترى؟ هذا هو أكثر في شخصية محمد أفدول.";
                                break;
                            case "Iggy":
                                msg = "Woof! Woof";
                                break;
                            case "Coco Jumbo":
                                msg = "...";
                                break;
                            case "Danny":
                                msg = "";
                                break;
                        }
                        break;
                    case TRADE_DECLINE:
                        switch (language) {
                            case BRITISH:
                                msg = "I'm disinclined to acquiesce to your request.";
                                break;
                            case US_AMERICAN:
                                msg = "Your offer is shit, buddy";
                                break;
                            case SIMPLE_ENGLISH:
                                msg = "I'm sorry. No";
                                break;
                            case ITALIAN:
                                msg = "Cosa ti ho mai fatto per farti mancare di rispetto in questo modo?";
                                break;
                            case JAPANESE:
                                msg = "何れまたの機会にでも。";
                                break;
                        }
                        switch (name) {
                            case "Rudol von Stroheim":
                                msg = "Ich mag zwar nicht so aussehen, aber ich bin ein stolzer Deutscher Soldat und dein Angebot beleidigt mich.";
                                break;
                            case "Jean Pierre Polnareff":
                                msg = "Je vais te transformer en pelote d'épingles !";
                                break;
                            case "Muhammad Avdol":
                                msg = "هل ستتحدى العراف في معركة التنبؤ؟ من تظن نفسك؟";
                                break;
                            case "Iggy":
                            case "Danny":
                                msg = "Woof";
                                break;
                            case "Coco Jumbo":
                                msg = "...";
                                break;
                            case "Giorno Giovanna":
                                msg = "Oh please. Did you really think you're that lucky. A piece of shit like you?";
                                break;
                        }
                        break;
                    case GAME_WIN:
                        switch (language) {
                            case BRITISH:
                                msg = "Remember today. Today, life is good.";
                                break;
                            case US_AMERICAN:
                                msg = "Too easy";
                                break;
                            case SIMPLE_ENGLISH:
                                msg = "I won. Yes!";
                                break;
                            case ITALIAN:
                                msg = "La dea della vittoria brilla su di me";
                                break;
                            case JAPANESE:
                                msg = "お前はもう死んでいる。";
                                break;
                        }
                        switch (name) {
                            case "Rudol von Stroheim":
                                msg = "DEUTSCHE WISSENSCHAFT IST DIE BESTE DER WELT";
                                break;
                            case "Jean Pierre Polnareff":
                                msg = "Un dernier souffle pathétique ? Mourrez-vous tranquillement, voulez-vous ?";
                                break;
                            case "Muhammad Avdol":
                                msg = "نعم أنا!";
                                break;
                            case "Iggy":
                            case "Danny":
                                msg = "Wooof!";
                                break;
                            case "Coco Jumbo":
                                msg = "...";
                                break;
                            case "Giorno Giovanna":
                                msg = "This is... Requiem.";
                                break;
                        }
                        break;
                    case GAME_LOSE:
                        switch (language) {
                            case BRITISH:
                                msg = "You have triumphed over us. The day is yours.";
                                break;
                            case US_AMERICAN:
                                msg = "Shithead";
                                break;
                            case SIMPLE_ENGLISH:
                                msg = "Sad life.";
                                break;
                            case ITALIAN:
                                msg = "Dio mio!";
                                break;
                            case JAPANESE:
                                msg = "余が認めた以上に遙かに賢明でござった。";
                                break;
                        }
                        switch (name) {
                            case "Rudol von Stroheim":
                                msg = "Junge! Unsere Rassen mögen verschieden sein, aber ich respektiere solche wie dich, die Mut haben! Nur der Große sollte leben!... Tötet alle außer ihm.";
                                break;
                            case "Jean Pierre Polnareff":
                                msg = "Je vais mourir comme ça. En reconnaissance de votre force. Se suicider à ce stade serait déshonorant.";
                                break;
                            case "Muhammad Avdol":
                                msg = "بولناريف ، إيجي ، انتبه!";
                                break;
                            case "Iggy":
                            case "Danny":
                                msg = "Wooof.";
                                break;
                            case "Coco Jumbo":
                                msg = "...";
                                break;
                        }
                        break;
                    case TAX:
                        if ((int) (Math.random() * 100) < 60) break;
                        switch (language) {
                            case BRITISH:
                                msg = "I pay what I must";
                                break;
                            case US_AMERICAN:
                                msg = "Taxation is theft";
                                break;
                            case SIMPLE_ENGLISH:
                                msg = "A small price to pay for salvation";
                                break;
                            case ITALIAN:
                                msg = "I miei soldi duramente guadagnati!";
                                break;
                            case JAPANESE:
                                msg = "それは私の勝利を危うくするものではありません。";
                                break;
                        }
                        switch (name) {
                            case "Rudol von Stroheim":
                                msg = "Ich würde ohne Probleme 2 oder 3 Gliedmaßen für mein Land geben!";
                                break;
                            case "Jean Pierre Polnareff":
                                msg = "C'est tellement ennuyeux... C'EST TELLEMENT ENNUYEUX!!!";
                                break;
                            case "Muhammad Avdol":
                                msg = "ألم يكن إيسوب الذي قال \"الرياح الباردة تضيف ملابس لكن الحرارة تجعلك تصرخ\"؟";
                                break;
                            case "Iggy":
                            case "Danny":
                                msg = "Woof Woof.";
                                break;
                            case "Coco Jumbo":
                                msg = "...";
                                break;
                        }
                        break;
                    case MONOPOLY:
                        switch (language) {
                            case BRITISH:
                                msg = "Your resources will be used wisely";
                                break;
                            case US_AMERICAN:
                                msg = "Yoink";
                                break;
                            case SIMPLE_ENGLISH:
                                msg = "Gimme all your resources";
                                break;
                            case ITALIAN:
                                msg = "Le tue risorse sono le mie risorse";
                                break;
                            case JAPANESE:
                                msg = "あなたのリソースを教えてください";
                                break;
                        }
                        switch (name) {
                            case "Rudol von Stroheim":
                                msg = "Ich bin anders als ihr feigen britischen Weicheier.";
                                break;
                            case "Jean Pierre Polnareff":
                                msg = "Je veux être plus grand que Disney! Pas n'importe quel artiste affamé! Je veux construire Polnareff Land!";
                                break;
                            case "Muhammad Avdol":
                                msg = "سأنهي عليك وأعطيك الجحيم !! الجحيم 2 يو!";
                                break;
                            case "Iggy":
                            case "Danny":
                                msg = "Woof";
                                break;
                            case "Coco Jumbo":
                                msg = "...";
                                break;
                        }
                        break;
                    case MOVE_ROBBER:
                        if ((int) (Math.random() * 100) < 60) break;
                        switch (language) {
                            case BRITISH:
                                msg = "Those who can't use their head must use their legs.";
                                break;
                            case US_AMERICAN:
                                msg = "Get him away from me";
                                break;
                            case SIMPLE_ENGLISH:
                                msg = "A 7 a day keeps the robber away";
                                break;
                            case ITALIAN:
                                msg = "Nessun rapinatore, nessun problema";
                                break;
                            case JAPANESE:
                                msg = "アタック!";
                                break;
                        }
                        switch (name) {
                            case "Rudol von Stroheim":
                                msg = "Die Größe der Menschen ist, wenn sie der Angst mit Stolz begegnen.";
                                break;
                            case "Jean Pierre Polnareff":
                                msg = "Avec de si jolies jambes, il nous faut une photo de tout le corps !";
                                break;
                            case "Muhammad Avdol":
                                msg = "قد أضطر إلى الحصول على القليل من الخشونة. أحيانًا تكون المعاناة هي الطريقة الوحيدة لجعل الأحمق يرى السبب.";
                                break;
                            case "Iggy":
                            case "Danny":
                                msg = "Woof woof!";
                                break;
                            case "Coco Jumbo":
                                msg = "...";
                                break;
                        }
                    default:
                }
                break;
            case HARD:
                switch (type) {
                    case START:
                        switch (language) {
                            case BRITISH:
                                msg = "Let's not bother with introductions. It won't take that long.";
                                break;
                            case US_AMERICAN:
                                msg = "I have come here to chew gum and kick ass... and I'm all out of gum";
                                break;
                            case ARABIC:
                                msg = "دعونا لا نهتم بالمقدمات. لن يستغرق الأمر كل هذا الوقت.";
                                break;
                            case AZTEC:
                                msg = "Hueli qui qualli! Chicahuac \"-can”, ma in nen eztli!, moch la-loca ni-ameya “ateh” hueiatl! ";
                                break;
                        }
                        switch (name) {
                            case "Bug-Eaten":
                            case "Not Bug-Eaten":
                                msg = "*squeak*";
                                break;
                            case "Pet Shop":
                                msg = "*bird screech*";
                                break;
                            case "Stray Cat":
                                msg = "*cat purr*";
                                break;
                            case "Yoshikage Kira":
                                msg = "私の名は『吉良吉影』　年齢３３歳 自宅は杜王町北東部の別荘地帯にあり…　結婚はしていない…\n" + "\n" + "仕事は『カメユーチェーン店』の会社員で　毎日遅くとも夜８時までには帰宅する タバコは吸わない　酒はたしなむ程度　夜１１時には床につき　必ず８時間は睡眠をとるようにしている…\n" + "\n" + "寝る前にあたたかいミルクを飲み　２０分ほどのストレッチで体をほぐしてから床につくと　ほとんど朝まで熟睡さ… 赤ん坊のように疲労やストレスを残さずに　朝　目を覚ませるんだ… 健康診断でも異常なしと言われたよ\n" + "\n" + "わたしは常に『心の平穏』を願って生きてる人間ということを説明しているのだよ…\n" + "\n" + "『勝ち負け』にこだわったり　頭をかかえるような『トラブル』とか　夜もねむれないといった『敵』をつくらない…というのが わたしの社会に対する姿勢であり　それが自分の幸福だということを知っている…\n" + "\n" + "もっとも　闘ったとしても　わたしは誰にも負けんがね";
                                break;
                            case "Head Doctor":
                                msg = "私を追いかけるために立ち上がってくれることは間違いありません。";
                                break;
                            case "Diavolo":
                                msg = "Posso vedere esattamente come vi comporterete! Questa è l'abilità del mio Re Cremisi!";
                                break;
                            case "Polpo":
                                msg = "Quando si deve scegliere da una lista di candidati... qual è secondo lei la qualità più importante da notare? [...] Cioè la fiducia";
                                break;
                            case "Doppio":
                                msg = "Tulululululu! TULULULULULULULU!!! Cliccare Sì? Questo è Doppio.";
                                break;
                            case "Temmo":
                                msg = "Enums Enums ENUMS ENUMS EEEEEENUUUUUUMS";
                                break;
                        }
                        break;
                    case TRADE_ACCEPT:
                        switch (language) {
                            case BRITISH:
                                msg = "It will benefit me";
                                break;
                            case US_AMERICAN:
                                msg = "Do not think I care about you";
                                break;
                            case ARABIC:
                                msg = "سوف يفيدني ذلك";
                                break;
                            case AZTEC:
                                msg = "Hueli qui qualli ca.";
                                break;
                        }
                        switch (name) {
                            case "Bug-Eaten":
                            case "Not Bug-Eaten":
                                msg = "*high squeak*";
                                break;
                            case "Pet Shop":
                                msg = "...";
                                break;
                            case "Stray Cat":
                                msg = "Meow";
                                break;
                            case "Yoshikage Kira":
                                msg = "穏やかな気分で人生を送れそうです。";
                                break;
                            case "Head Doctor":
                                msg = "最後の最後で災難に遭わないように気をつけましょう、あなたも私も。";
                                break;
                            case "Diavolo":
                                msg = "Questa è una prova... La prova di sconfiggere il mio passato... Lo accetto";
                                break;
                            case "Polpo":
                                msg = "Non lottiamo per soldi o profitti, né rischiamo la vita o scateniamo rivolte perché qualche pazzo ci ha preso il posto sull'autobus o i biglietti per il concerto.";
                                break;
                            case "Doppio":
                                msg = "Ricevuto.";
                                break;
                            case "Temmo":
                                msg = "Wow! Nicht erwartet, dass etwas Vernünftiges von dir kommt";
                                break;
                        }
                        break;
                    case TRADE_DECLINE:
                        switch (language) {
                            case BRITISH:
                                msg = "Your offer insults me.";
                                break;
                            case US_AMERICAN:
                                msg = "You want me to accept that? You dumb or what?";
                                break;
                            case ARABIC:
                                msg = "عرضك يهينني.";
                                break;
                            case AZTEC:
                                msg = "Melahuac amo!";
                                break;
                        }
                        switch (name) {
                            case "Bug-Eaten":
                            case "Not Bug-Eaten":
                                msg = "*low squeak*";
                                break;
                            case "Pet Shop":
                                msg = "...";
                                break;
                            case "Stray Cat":
                                msg = "*hiss*";
                                break;
                            case "Yoshikage Kira":
                                msg = "誰かに相談する前に、私があなたを消します。";
                                break;
                            case "Head Doctor":
                                msg = "あなたの目はまだ動いていますか？これが最後だと言われたらどう思いますか？";
                                break;
                            case "Diavolo":
                                msg = "Non ho mai previsto che qualcuno sarebbe stato così sciocco da tradire la mia banda.";
                                break;
                            case "Polpo":
                                msg = "Se la cosa più importante a questo mondo è la fiducia, allora, al contrario, la cosa più detestabile al mondo è insultare questa fiducia.";
                                break;
                            case "Doppio":
                                msg = "No, mi dispiace.";
                                break;
                            case "Temmo":
                                msg = "Als würde ich sowas annehmen";
                                break;
                        }
                        break;
                    case GAME_WIN:
                        switch (language) {
                            case BRITISH:
                                msg = "Quite easy I dare say";
                                break;
                            case US_AMERICAN:
                                msg = "Git gud.";
                                break;
                            case ARABIC:
                                msg = "من السهل جدا أن أقول";
                                break;
                            case AZTEC:
                                msg = "Senka uel ayoui nidare tlaihto";
                                break;
                        }
                        switch (name) {
                            case "Bug-Eaten":
                            case "Not Bug-Eaten":
                                msg = "*squeak* *squeak*";
                                break;
                            case "Pet Shop":
                                msg = "*bird screech*";
                                break;
                            case "Stray Cat":
                                msg = "Meow meeeow";
                                break;
                            case "Yoshikage Kira":
                                msg = "私にとっては、勝つとか負けるとかはあまり関係ありません。ただ、生き残りたい。穏やかな生活を送りたい。殺すのが性に合っているというだけだ。私は幸せな人生を送りますよ";
                                break;
                            case "Head Doctor":
                                msg = "長かった旅も終わり、私は「新ロカカ」を手に入れ、あなたも私ももうすぐ故郷に帰ることになる...。";
                                break;
                            case "Diavolo":
                                msg = "Nessuno può sfuggire dal destino scelto \n" + "Rimane solo il risultato che voi sarete distrutti\n" + "L’eterna cima esiste solo per me \n" + "Puoi cantare canzoni di tristezza nel mondo senza tempo";
                                break;
                            case "Polpo":
                                msg = "Gli esseri umani spesso... dicono qualcosa e fanno il contrario. Questo è il bello degli umani, anche se altrettanto spesso può essere il loro lato brutto...";
                                break;
                            case "Doppio":
                                msg = "Tulululululu! TULULULULULULULU!!! Cliccare Sì? Questo è Doppio.";
                                break;
                            case "Temmo":
                                msg = "Dein Code ist Müll";
                                break;
                        }
                        break;
                    case GAME_LOSE:
                        switch (language) {
                            case BRITISH:
                                msg = "Bloody hell... A smooth-brained knobhead like you defeated me?!";
                                break;
                            case US_AMERICAN:
                                msg = "CHEATER! No way I could have lost";
                                break;
                            case ARABIC:
                                msg = "والله تسلك نفسك. سوف أعطيك طعم حذائي";
                                break;
                            case AZTEC:
                                msg = "Huitzilopochtli pohpolhuia toahchicahua huan";
                                break;
                        }
                        switch (name) {
                            case "Bug-Eaten":
                            case "Not Bug-Eaten":
                                msg = "...";
                                break;
                            case "Pet Shop":
                                msg = "kack-kack-kack";
                                break;
                            case "Stray Cat":
                                msg = "*bass sound*";
                                break;
                            case "Yoshikage Kira":
                                msg = "私にとっては、勝つとか負けるとかはあまり関係ありません。ただ、生き残りたい。穏やかな生活を送りたい。殺すのが性に合っているというだけだ。私は幸せな人生を送りますよ";
                                break;
                            case "Head Doctor":
                                msg = "うーん...だから、個人的には...こんなことは初めてなので、ちょっとびっくりしています。";
                                break;
                            case "Diavolo":
                                msg = "Quante volte ancora morirò? Da dove verrà la prossima volta...? Qu... quando colpirà la prossima volta? I... I...!";
                                break;
                            case "Polpo":
                                msg = "Sento che anche Dio permetterebbe l'omicidio, in questa situazione!";
                                break;
                            case "Doppio":
                                msg = "Ultimamente la fortuna non è stata con me. È solo un problema dopo l'altro, e niente va mai bene.";
                                break;
                            case "Temmo":
                                msg = "Hax!";
                                break;
                        }
                        break;
                    case TAX:
                        if ((int) (Math.random() * 100) < 60) break;
                        switch (language) {
                            case BRITISH:
                                msg = "Why do I have to pay someone?";
                                break;
                            case US_AMERICAN:
                                msg = "Those taxes are too damn high!";
                                break;
                            case ARABIC:
                                msg = "لماذا علي أن أدفع لشخص ما؟";
                                break;
                            case AZTEC:
                                msg = "Nican aoc seneca Huitzilopochtli";
                                break;
                        }
                        switch (name) {
                            case "Bug-Eaten":
                            case "Not Bug-Eaten":
                                msg = "*squeak*";
                                break;
                            case "Pet Shop":
                                msg = "...";
                                break;
                            case "Stray Cat":
                                msg = "*hiss*";
                                break;
                            case "Yoshikage Kira":
                                msg = "俺、吉良吉影は、ただ静かに暮らしたいだけなのに、こいつらが邪魔をしてくるんだ。";
                                break;
                            case "Head Doctor":
                                msg = "...もしかして... ここに来たのは... 私を追いかけるつもりで？";
                                break;
                            case "Diavolo":
                                msg = "I vostri grugniti con le vostre sporche abilità e i vostri insignificanti intelletti non saranno mai in grado di superare le previsioni di King Crimson... né sarete in grado di aggirarle!";
                                break;
                            case "Polpo":
                                msg = "Il sentiero della morte!";
                                break;
                            case "Doppio":
                                msg = "Mi manchi... Capo... chiamami... come fai sempre... Ti aspetto... Chiama... me...";
                                break;
                            case "Temmo":
                                msg = "Aufploppende Fenster sind schrecklich";
                                break;
                        }
                        break;
                    case MONOPOLY:
                        switch (language) {
                            case BRITISH:
                                msg = "Pay your tribute to your new suzerain!";
                                break;
                            case US_AMERICAN:
                                msg = "Consider it as protection money";
                                break;
                            case ARABIC:
                                msg = "اعتبرها أموال حماية";
                                break;
                            case AZTEC:
                                msg = "Xi-miqa-can! Xi-miqa-can! Xi-miqa-can!";
                                break;
                        }
                        switch (name) {
                            case "Bug-Eaten":
                            case "Not Bug-Eaten":
                                msg = "*high squeak*";
                                break;
                            case "Pet Shop":
                                msg = "...";
                                break;
                            case "Stray Cat":
                                msg = "Meow";
                                break;
                            case "Yoshikage Kira":
                                msg = "どう言えばいいのかな？ちょっと下品ですが...へへへ...。私は...BONERを手に入れた";
                                break;
                            case "Head Doctor":
                                msg = "あなたが所有することを許可されたものは何もありません。それが梨園であろうと、一本のニューロカカであろうと......!!!";
                                break;
                            case "Diavolo":
                                msg = "La mia abilità sta sull'apice della realtà!";
                                break;
                            case "Polpo":
                                msg = "Quando sentiamo che la nostra fiducia è stata insultata, abbiamo la sanzione di mettere in gioco delle vite";
                                break;
                            case "Doppio":
                                msg = "Seguire gli ordini del Boss è la mia ragione di vita";
                                break;
                            case "Temmo":
                                msg = "Gib her, damit kannst du eh nix anfangen";
                                break;
                        }
                        break;
                    case MOVE_ROBBER:
                        if ((int) (Math.random() * 100) < 60) break;
                        switch (language) {
                            case BRITISH:
                                msg = "Move that peasant away";
                                break;
                            case US_AMERICAN:
                                msg = "Get off my lawn!";
                                break;
                            case ARABIC:
                                msg = "انقل هذا الفلاح بعيدًا";
                                break;
                            case AZTEC:
                                msg = "Tlein ticnequi. Tiyaochihuani ahnozo temauhqui.";
                                break;
                        }
                        switch (name) {
                            case "Bug-Eaten":
                            case "Not Bug-Eaten":
                                msg = "*squeak*";
                                break;
                            case "Pet Shop":
                                msg = "...";
                                break;
                            case "Stray Cat":
                                msg = "*purrs*";
                                break;
                            case "Yoshikage Kira":
                                msg = "最近、いろいろなことがあって、人生の波が荒く激しくなってきた...。";
                                break;
                            case "Head Doctor":
                                msg = "私を追いかけるために立ち上がってくれることは間違いありません。";
                                break;
                            case "Diavolo":
                                msg = "Sei tutto un viscido muco vomitato nella tazza del cesso! Come ti permetti di farmi questo!";
                                break;
                            case "Polpo":
                                msg = "Ti darò la possibilità... di vedere i due sentieri che puoi seguire... il primo sentiero per gli eletti che vivranno... e l'altro! Il sentiero della morte!";
                                break;
                            case "Doppio":
                                msg = "Eseguire gli ordini è anche la mia ragione di vita.";
                                break;
                            case "Temmo":
                                msg = "Das sollte man in der Config-Datei ausschalten können";
                                break;
                        }
                        break;
                }
                break;
        }
        return msg;
    }
}