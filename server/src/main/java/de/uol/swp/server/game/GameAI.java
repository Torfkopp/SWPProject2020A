package de.uol.swp.server.game;

import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.map.hexes.IGameHex;
import de.uol.swp.common.game.map.hexes.IHarbourHex;
import de.uol.swp.common.game.map.hexes.IResourceHex;
import de.uol.swp.common.game.map.management.IEdge;
import de.uol.swp.common.game.map.management.IIntersection;
import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.game.message.AbstractGameMessage;
import de.uol.swp.common.game.message.BuildingSuccessfulMessage;
import de.uol.swp.common.game.message.UpdateUniqueCardsListMessage;
import de.uol.swp.common.game.request.BuyDevelopmentCardRequest;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.Inventory;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.IResource;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.game.robber.RobberPositionMessage;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.AI;
import de.uol.swp.server.game.map.IGameMapManagement;
import de.uol.swp.server.lobby.LobbyService;

import java.util.*;

import static de.uol.swp.common.game.message.BuildingSuccessfulMessage.Type.*;
import static de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType.*;

/**
 * Class for all AI related methods
 *
 * @author Mario Fokken
 * @since 2021-05-15
 */
public class GameAI {

    private final GameService gameService;
    private final IGameManagement gameManagement;
    private final LobbyService lobbyService;

    private final Map<AI, List<IHarbourHex.HarbourResource>> harbours = new HashMap<>();
    private final Map<Game, Map<MapPoint, Integer>> aiBuildPriority = new HashMap<>();

    /**
     * Enum for all types of
     * ChatMessages an AI can write
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    protected enum WriteType {
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
     * Constructor
     *
     * @param gameService    The GameService
     * @param gameManagement The GameService's gameManagement
     * @param lobbyService   The GameService's lobbyService
     *
     * @since 2021-05-15
     */
    public GameAI(GameService gameService, IGameManagement gameManagement, LobbyService lobbyService) {
        this.gameService = gameService;
        this.gameManagement = gameManagement;
        this.lobbyService = lobbyService;
    }

    /**
     * Helper method to move the robber when
     * an AI gets a seven.
     *
     * @author Mario Fokken
     * @since 2021-05-11
     */
    void robberMovementAI(AI uehara, LobbyName lobby) {
        Game game = gameManagement.getGame(lobby);
        IGameMapManagement map = game.getMap();
        AI.Difficulty difficulty = uehara.getDifficulty();
        writeChatMessageAI(uehara, lobby, WriteType.MOVE_ROBBER);

        //Pick place to put the robber on
        int y = 3;
        int x = 3;
        MapPoint mapPoint;
        switch (difficulty) {
            case EASY:
                y = (int) (Math.random() * 4 + 1);
                x = (y == 1 || y == 5) ? ((int) (Math.random() * 3 + 1)) :
                    ((y == 2 || y == 4) ? ((int) (Math.random() * 4 + 1)) : ((int) (Math.random() * 5 + 1)));
                break;
            case HARD:
                Map<MapPoint, Integer> position = new HashMap<>() {};
                Player ai = game.getPlayer(uehara);
                int rating;
                //Get every hex and give them a rating depending on the amount of players around it
                for (int i = 1; i < 6; i++) {
                    for (int j = 1; j < 6; j++) {
                        if (((i == 1 || i == 5) && j > 3) || ((i == 2 || i == 4) && j > 4)) break;
                        rating = 0;
                        mapPoint = MapPoint.HexMapPoint(i, j);
                        for (Player p : map.getPlayersAroundHex(mapPoint)) {
                            if (p.equals(ai)) {
                                rating -= 2;
                                continue;
                            }
                            rating++;
                        }
                        position.put(mapPoint, rating);
                    }
                }
                if (position.containsValue(3)) rating = 3;
                else if (position.containsValue(2)) rating = 2;
                else if (position.containsValue(1)) rating = 1;
                else rating = 0;
                //Filter every hex with a rating lower than the highest
                List<MapPoint> points = new ArrayList<>(position.keySet());
                for (MapPoint mp : points) if (position.get(mp) < rating) position.remove(mp);
                //Pick a random hex from the survivors
                if (!position.isEmpty()) {
                    mapPoint = new ArrayList<>(position.keySet()).get((int) (Math.random() * position.keySet().size()));
                    y = mapPoint.getY();
                    x = mapPoint.getX();
                }
        }
        mapPoint = MapPoint.HexMapPoint(y, x);
        GameService.LOG.debug("{} moves the robber to position: {}|{}", uehara, y, x);
        map.moveRobber(mapPoint);
        GameService.LOG.debug("Sending RobberPositionMessage for Lobby {}", lobby);
        AbstractGameMessage msg = new RobberPositionMessage(lobby, uehara, mapPoint);
        lobbyService.sendToAllInLobby(lobby, msg);

        //Pick victim to steal random card from
        List<Player> player = new ArrayList<>(map.getPlayersAroundHex(mapPoint));
        if (player.size() > 0) {
            Player victim = player.get(0);
            switch (difficulty) {
                case EASY:
                    victim = player.get((int) (Math.random() * player.size()));
                    break;
                case HARD:
                    Map<Player, List<MapPoint>> victimRating = map.getPlayerSettlementsAndCities();
                    for (Player p : victimRating.keySet())
                        //Change victim when p has more cities/settlements or,
                        //if amount is the same, when p has more resources
                        if ((victimRating.get(p).size() > victimRating.get(victim).size()) || //
                            ((victimRating.get(p).size() == victimRating.get(victim).size()) && //
                             (game.getInventory(p).getResourceAmount() > game.getInventory(victim)
                                                                             .getResourceAmount()))) victim = p;
                    break;
            }
            gameService.robRandomResource(lobby, uehara, game.getUserFromPlayer(victim));
        }
    }

    /**
     * Helper method to pay the tax for an AI
     *
     * @param game   The game the AI is in
     * @param uehara The AI to pay the tax
     *
     * @author Mario Fokken
     * @since 2021-05-11
     */
    void taxPayAI(Game game, AI uehara) {
        Inventory inv = game.getInventory(uehara);
        int i = inv.getResourceAmount() / 2;
        writeChatMessageAI(uehara, game.getLobby().getName(), WriteType.TAX);

        GameService.LOG.debug("{} has to give up {} of their {} cards", uehara, i, inv.getResourceAmount());
        switch (uehara.getDifficulty()) {
            case EASY:
                while (i > 0) {
                    for (ResourceType resourceType : ResourceType.values()) {
                        if (inv.get(resourceType) > 0) {
                            inv.increase(resourceType, -1);
                            i--;
                            if (i == 0) break;
                        }
                    }
                }
                break;
            case HARD:
                ResourceType highest = BRICK;
                while (i > 0) {
                    for (IResource r : inv.getResources())
                        if (r.getAmount() > inv.getResources().getAmount(highest)) highest = r.getType();
                    inv.decrease(highest);
                    i--;
                }
        }
    }

    /**
     * Helper method to calculate if the AI
     * wants to accept the trade offer
     *
     * @param uehara   The AI to decide
     * @param lobby    The lobby
     * @param offered  The offered Resources
     * @param demanded The demanded Resources
     *
     * @return If AI accepts
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    boolean tradeAcceptationAI(AI uehara, LobbyName lobby, ResourceList offered, ResourceList demanded) {
        int difference = offered.getTotal() - demanded.getTotal();
        switch (uehara.getDifficulty()) {
            case EASY:
                if (uehara.getUsername().equals("Robert E. O. Speedwagon")) return true;
                //Difference:4-100%, 3-92%, 2-84%, 1-76%, 0-68%
                if (difference >= 0 && ((int) (Math.random() * 100) < (68 + difference * 8))) return true;
                    //Difference:4-0%, 3-8%, 2-16%, 1-24%
                else return difference < 0 && ((int) (Math.random() * 100) < (32 - difference * 8));
            case HARD:
                if (demanded.getTotal() == 0 || difference > 2) return true;
                if (offered.getTotal() == 0 || difference < -2) return false;
                int rating = 0;
                Game game = gameManagement.getGame(lobby);
                //If the AI has no resource left after trade, the rating goes down
                for (IResource r : offered)
                    if (game.getInventory(uehara).get(r.getType()) - (r.getAmount() - demanded
                            .getAmount(r.getType())) == 0) rating--;
                List<ResourceType> priority = new ArrayList<>();
                //Prioritise Lumber, Brick early; ore later
                if (game.getRound() <= 4) {
                    priority.add(LUMBER);
                    priority.add(BRICK);
                } else priority.add(ORE);
                //Prioritise resource if low on it
                for (IResource r : game.getInventory(uehara).getResources())
                    if (r.getAmount() <= 1) priority.add(r.getType());
                //If offered has a prioritised resource, the rating goes up
                for (ResourceType r : priority) rating += offered.getAmount(r) - demanded.getAmount(r);
                return ((int) (Math.random() * 100)) < (50 + rating * 10);
            default:
                return false;
        }
    }

    /**
     * Helper method for an AI's turn
     *
     * @param game   The game the AI is in
     * @param uehara The AI to make its turn
     *
     * @author Mario Fokken
     * @since 2021-05-11
     */
    void turnAI(Game game, AI uehara) {
        switch (uehara.getDifficulty()) {
            case EASY:
                turnPlayCardsAIEasy(game, uehara);
                turnBuildAIEasy(game, uehara);
                break;
            case HARD:
                if (game.getInventory(uehara).get(DevelopmentCardType.KNIGHT_CARD) > 0)
                    playCardAI(game, uehara, DevelopmentCardType.KNIGHT_CARD, null, null);
                turnBuildAIHard(game, uehara);
                break;
        }
        //Trying to end the turn
        gameService.turnEndAI(game, uehara);
    }

    /**
     * Helper method to make a chat
     * message for an AI
     * <p>
     * It looks like shit, but I don't care
     *
     * @param uehara    The AI to send the message
     * @param lobbyName The lobby the AI is in
     * @param type      The type of chat message
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    void writeChatMessageAI(AI uehara, LobbyName lobbyName, WriteType type) {
        String msg = "";
        String name = uehara.getUsername();
        AI.Language language = uehara.getLanguage();
        switch (uehara.getDifficulty()) {
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
                        if (name.equals("Rudol von Stroheim"))
                            msg = "Ich würde ohne Probleme 2 oder 3 Gliedmaßen für mein Land geben!";
                        else if (name.equals("Jean Pierre Polnareff"))
                            msg = "C'est tellement ennuyeux... C'EST TELLEMENT ENNUYEUX!!!";
                        else if (name.equals("Muhammad Avdol"))
                            msg = "ألم يكن إيسوب الذي قال \"الرياح الباردة تضيف ملابس لكن الحرارة تجعلك تصرخ\"؟";
                        else if (name.equals("Iggy") || name.equals("Danny")) msg = "Woof Woof.";
                        else if (name.equals("Coco Jumbo")) msg = "...";
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
        gameService.postAI(uehara, msg, lobbyName);
    }

    /**
     * Helper method to build a
     * city for a hard AI
     * <p>
     * It upgrades the settlement on the most
     * lucrative spot.
     *
     * @param game   The game the AI is in
     * @param uehara The AI to build the city
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private boolean buildCity(Game game, AI uehara) {
        Inventory inv = game.getInventory(uehara);

        //Play a monopoly card if possible
        if (inv.get(DevelopmentCardType.MONOPOLY_CARD) > 1) {
            if (inv.get(ORE) < 3) playCardAI(game, uehara, DevelopmentCardType.MONOPOLY_CARD, ORE, null);
            if (inv.get(GRAIN) < 2) playCardAI(game, uehara, DevelopmentCardType.MONOPOLY_CARD, GRAIN, null);
        }

        //Not enough Resources
        if (inv.get(GRAIN) < 2 || inv.get(ORE) < 3) return false;

        IGameMapManagement map = game.getMap();
        LobbyName lobbyName = game.getLobby().getName();
        Player ai = game.getPlayer(uehara);
        Map<MapPoint, Integer> settlements = new HashMap<>();

        //Fill settlements with every settlement of the AI
        MapPoint mapPoint = null;
        for (MapPoint mp : map.getPlayerSettlementsAndCities().get(ai)) {
            for (MapPoint mp2 : aiBuildPriority.get(game).keySet())
                if (mp.getY() == mp2.getY() && mp.getX() == mp2.getX()) mapPoint = mp2;
            if (mapPoint == null) continue;
            if (map.getIntersection(mp).getState() == IIntersection.IntersectionState.SETTLEMENT)
                settlements.put(mp, aiBuildPriority.get(game).get(mapPoint));
        }

        //Pick most lucrative spot and upgrade the settlement
        for (int i = 0; i < 15; i++) {
            if (settlements.containsValue(i)) for (MapPoint mp : settlements.keySet())
                if (settlements.get(mp) == i) {
                    map.upgradeSettlement(ai, mp);
                    inv.decrease(GRAIN, 2);
                    inv.decrease(ORE, 3);
                    lobbyService
                            .sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, uehara, mp, CITY));
                    return true;
                }
        }
        return false;
    }

    /**
     * Helper method to build a
     * road for a hard AI
     * <p>
     * It builds roads to reach
     * the most lucrative spot in vicinity
     *
     * @param game   The game the AI is in
     * @param uehara The AI to build the city
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private boolean buildRoad(Game game, AI uehara) {
        Inventory inv = game.getInventory(uehara);
        int roadsPlaceable = Math.min(inv.get(LUMBER), inv.get(BRICK));
        if (roadsPlaceable == 0) return false;

        IGameMapManagement map = game.getMap();
        Player ai = game.getPlayer(uehara);
        MapPoint mapPoint;
        List<MapPoint> intersections = new ArrayList<>(map.getPlayerSettlementsAndCities().get(ai));
        //Gets all intersections "under AI's control" (settlement, city, or adjacent road)
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 11; j++) {
                if ((i == 0 || i == 5) && j > 6) break;
                else if ((i == 1 || i == 4) && j > 8) break;
                mapPoint = MapPoint.IntersectionMapPoint(i, j);
                for (IEdge edge : map.getEdgesAroundIntersection(map.getIntersection(mapPoint))) {
                    if (edge.getOwner() == ai) intersections.add(mapPoint);
                }
            }

        Map<MapPoint, Integer> lucrativeSpots = new HashMap<>();
        int i = 0;
        //Gets lucrative spots from priority map
        do {
            for (MapPoint mp : aiBuildPriority.get(game).keySet())
                if (aiBuildPriority.get(game).get(mp) <= 3 + i && map.getIntersection(mp)
                                                                     .getState() == IIntersection.IntersectionState.FREE)
                    lucrativeSpots.put(mp, aiBuildPriority.get(game).get(mp));
            i++;
        } while (lucrativeSpots.isEmpty());

        List<List<MapPoint>> roads = new ArrayList<>();
        List<MapPoint> path;

        //Get all path to the lucrativeSpot(s)
        for (MapPoint mp : intersections) {
            for (MapPoint mp2 : lucrativeSpots.keySet()) {
                path = findPath(game, uehara, mp, mp2);
                if (path != null) roads.add(path);
            }
        }

        //Play a card if possible
        if (inv.get(DevelopmentCardType.ROAD_BUILDING_CARD) > 0)
            playCardAI(game, uehara, DevelopmentCardType.ROAD_BUILDING_CARD, null, null);
        if (inv.get(DevelopmentCardType.YEAR_OF_PLENTY_CARD) > 0)
            playCardAI(game, uehara, DevelopmentCardType.YEAR_OF_PLENTY_CARD, BRICK, LUMBER);

        //Chooses the best path; best as in longest buildable road
        path = null;
        for (List<MapPoint> list : roads) {
            if (path == null) path = list;
            else if (list.size() > path.size() && list.size() <= roadsPlaceable) path = list;
        }

        //Build the road
        if (path != null) {
            buildRoadOnPath(game, uehara, path);
            return true;
        }

        return false;
    }

    /**
     * Helper method to build the roads
     * on a path
     *
     * @param game   The game the AI is in
     * @param uehara The AI to build the roads
     * @param path   The path to build the roads on
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private void buildRoadOnPath(Game game, AI uehara, List<MapPoint> path) {
        Inventory inv = game.getInventory(uehara);
        LobbyName lobbyName = game.getLobby().getName();
        MapPoint mapPoint;
        for (int i = 0; i < path.size() - 1; i++) {
            if (inv.get(BRICK) == 0 || inv.get(LUMBER) == 0) break;
            mapPoint = MapPoint.EdgeMapPoint(path.get(i), path.get(i + 1));
            if (game.getMap().roadPlaceable(game.getPlayer(uehara), mapPoint)) {
                game.getMap().placeRoad(game.getPlayer(uehara), mapPoint);
                inv.decrease(BRICK);
                inv.decrease(LUMBER);
                lobbyService
                        .sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, uehara, mapPoint, ROAD));
            }
        }
    }

    /**
     * Helper method to build a
     * settlement for a hard AI
     * <p>
     * It builds a settlement on the most
     * lucrative spot available.
     *
     * @param game   The game the AI is in
     * @param uehara The AI to build the settlement
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private boolean buildSettlement(Game game, AI uehara) {
        Inventory inv = game.getInventory(uehara);
        if (!(inv.get(LUMBER) >= 1 && inv.get(BRICK) >= 1 && inv.get(GRAIN) >= 1 && inv.get(WOOL) >= 1)) return false;
        IGameMapManagement map = game.getMap();
        List<MapPoint> intersections = new ArrayList<>();
        LobbyName lobbyName = game.getLobby().getName();
        Player ai = game.getPlayer(uehara);
        MapPoint mapPoint;

        //Fill intersections with all possible settlement locations
        for (int i = 0; i <= 5; i++)
            for (int j = 0; j <= 10; j++) {
                if ((i == 1 || i == 4) && j >= 9) break;
                else if ((i == 0 || i == 5) && j >= 7) break;
                mapPoint = MapPoint.IntersectionMapPoint(i, j);
                if (map.settlementPlaceable(ai, mapPoint)) intersections.add(mapPoint);
            }

        //Pick the most lucrative location and build the settlement
        MapPoint mp = null;
        for (int i = 0; i < 20; i++) {
            for (MapPoint mpI : intersections) {
                for (MapPoint mpP : aiBuildPriority.get(game).keySet())
                    if (mpI.getY() == mpP.getY() && mpI.getX() == mpP.getX()) {
                        mp = mpP;
                        break;
                    }
                if (aiBuildPriority.get(game).get(mp) == i) {
                    try {
                        map.placeSettlement(ai, mp);
                    } catch (GameMapManagement.SettlementMightInterfereWithLongestRoadException e) {
                        GameMapManagement.PlayerWithLengthOfLongestRoad a = map.findLongestRoad();
                        if (a.getLength() > 4) {
                            game.setPlayerWithLongestRoad(a.getPlayer());
                            game.setLongestRoadLength(a.getLength());
                        } else {
                            game.setPlayerWithLongestRoad(null);
                            game.setLongestRoadLength(0);
                        }
                        lobbyService.sendToAllInLobby(lobbyName, new UpdateUniqueCardsListMessage(lobbyName,
                                                                                                  game.getUniqueCardsList()));
                    }
                    if (map.getHarbourResource(mp) != null) {
                        harbours.putIfAbsent(uehara, new ArrayList<>());
                        harbours.get(uehara).add(map.getHarbourResource(mp));
                    }
                    inv.decrease(BRICK);
                    inv.decrease(LUMBER);
                    inv.decrease(GRAIN);
                    inv.decrease(WOOL);
                    lobbyService.sendToAllInLobby(lobbyName,
                                                  new BuildingSuccessfulMessage(lobbyName, uehara, mp, SETTLEMENT));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Helper method to fill the
     * aiBuildPriority map used by the
     * hard AI
     *
     * @param game The game the AI is in
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private void createBuildPriority(Game game) {
        IGameMapManagement map = game.getMap();
        MapPoint mapPoint;
        int rating;
        Map<MapPoint, Integer> priority = new HashMap<>();
        for (int i = 0; i <= 5; i++) {
            for (int j = 0; j <= 10; j++) {
                if ((i == 0 || i == 5) && j >= 7) break;
                else if ((i == 1 || i == 4) && j >= 9) break;
                mapPoint = MapPoint.IntersectionMapPoint(i, j);
                rating = 0;

                //Rate all locations
                for (MapPoint mp : game.getMap().getResourceHexesFromIntersection(mapPoint)) {
                    IResourceHex hex = (IResourceHex) game.getMap().getHex(mp);
                    rating += Math.abs(hex.getToken() - 7);
                }
                //Special rating for coast intersections
                if (i == 0 || i == 5 || j == 0 || j > 9 || ((i == 1 || i == 4) && j > 7)) {
                    if (map.getHarbourResource(mapPoint) == null) rating += 10;
                    else rating += 5;
                } else if (i == 3 && j == 3 && map.getHex(MapPoint.HexMapPoint(3, 3))
                                                  .getType() == IGameHex.HexType.DESERT) rating += 7;
                //↑ Special rating for the desert field (only if desert field is the on in the middle)
                priority.put(mapPoint, rating);
            }
        }
        aiBuildPriority.put(game, priority);
    }

    /**
     * Method to find a path
     * between two Intersections
     *
     * @param game   The Game the AI is in
     * @param uehara The AI to build the roads
     * @param start  The road's start
     * @param end    The road's end
     *
     * @return List of MapPoints; empty if no path is found
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private List<MapPoint> findPath(Game game, AI uehara, MapPoint start, MapPoint end) {
        List<MapPoint> path = new ArrayList<>(Collections.singletonList(start));
        return findPathRecursive(game.getMap(), game.getPlayer(uehara), path, end);
    }

    /**
     * Helper method for the helper method
     * to find a path between two Intersections
     *
     * @param map  The IGameMapManagement
     * @param ai   The AI wanting to build the road
     * @param path The taken path
     * @param end  The end point
     *
     * @return List of MapPoints
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private List<MapPoint> findPathRecursive(IGameMapManagement map, Player ai, List<MapPoint> path, MapPoint end) {
        if (path.size() > 11) return null;
        List<MapPoint> adjacent = new ArrayList<>();
        MapPoint start = path.get(path.size() - 1);
        MapPoint mapP;
        //Get the MapPoints of the adjacent Intersections
        for (IIntersection in : map.adjacentIntersections(map.getIntersection(start))) {
            mapP = map.getIntersectionMapPoint(in);
            //If end is near, take it
            if (mapP.getY() == end.getY() && mapP.getX() == end.getX()) {
                path.add(end);
                return path;
            }
            adjacent.add(mapP);
        }

        Map<MapPoint, List<MapPoint>> paths = new HashMap<>();

        //Remove all Intersection further away than the start or already used
        int yDiff = Math.abs(start.getY() - end.getY());
        int xDiff = Math.abs(start.getX() - end.getX());
        int distance = yDiff + xDiff;
        //If the distance is 0, we're at the end
        if (distance == 0) return path;

        List<MapPoint> removals = new ArrayList<>();
        for (MapPoint mapPoint : adjacent) {
            for (MapPoint mp : path) {
                if (mp.getY() == mapPoint.getY() && mp.getX() == mapPoint.getX()) {
                    removals.add(mapPoint);
                    break;
                }
            }
            if (Math.abs(mapPoint.getY() - end.getY()) > yDiff) removals.add(mapPoint);
            else if (mapPoint.getY() == end.getY())
                if (Math.abs(mapPoint.getX() - end.getX()) > xDiff) removals.add(mapPoint);
        }

        for (MapPoint mp : removals) adjacent.remove(mp);

        //Recursive call
        for (MapPoint mp : adjacent) {
            if ((map.getIntersection(mp).getState() == IIntersection.IntersectionState.FREE || //
                 map.getIntersection(mp).getOwner() == ai) && //
                (map.getEdge(MapPoint.EdgeMapPoint(start, mp)).getOwner() == null || //
                 map.getEdge(MapPoint.EdgeMapPoint(start, mp)).getOwner() == ai)) {
                paths.put(mp, path);
                paths.get(mp).add(mp);
                paths.put(mp, findPathRecursive(map, ai, paths.get(mp), end));
            } else paths.put(mp, null);
        }

        //Choose the best path
        List<MapPoint> path1 = null;
        List<MapPoint> path2;
        for (MapPoint mp : paths.keySet()) {
            if (path1 == null) path1 = paths.get(mp);
            else {
                path2 = paths.get(mp);
                if (path2 != null) path1 = path1.size() >= path2.size() ? path2 : path1;
            }
        }
        return path1;
    }

    /**
     * Helper method to let a hard AI
     * play a card
     *
     * @param game   The game the AI is in
     * @param uehara The AI to do the card playing
     * @param type   The type of card to play
     * @param res1   Needed for Monopoly, YearOfPlenty
     * @param res2   Only needed for YearOfPlenty
     *
     * @author Mario Fokken
     * @since 2021-05-16
     */
    private void playCardAI(Game game, AI uehara, DevelopmentCardType type, ResourceType res1, ResourceType res2) {
        Inventory inv = game.getInventory(uehara);
        LobbyName lobbyName = game.getLobby().getName();
        switch (type) {
            case KNIGHT_CARD:
                inv.decrease(DevelopmentCardType.KNIGHT_CARD);
                inv.increaseKnights();
                gameService.checkLargestArmy(lobbyName, uehara);
                gameService.updateVictoryPoints(lobbyName);
                robberMovementAI(uehara, lobbyName);
                break;
            case ROAD_BUILDING_CARD:
                inv.decrease(DevelopmentCardType.ROAD_BUILDING_CARD);
                inv.increase(BRICK, 2);
                inv.increase(LUMBER, 2);
                break;
            case YEAR_OF_PLENTY_CARD:
                inv.decrease(DevelopmentCardType.YEAR_OF_PLENTY_CARD);
                inv.increase(res1);
                inv.increase(res2);
                break;
            case MONOPOLY_CARD:
                inv.decrease(DevelopmentCardType.MONOPOLY_CARD);
                Inventory[] inventories = game.getAllInventories();
                for (Inventory i : inventories)
                    if (i.get(res1) > 0) {
                        inv.increase(res1, i.get(res1));
                        i.decrease(res1, i.get(res1));
                    }
                writeChatMessageAI(uehara, lobbyName, WriteType.MONOPOLY);
                break;
        }
    }

    /**
     * Helper method for an easy AI's
     * building phase
     *
     * @param game   The game the AI is in
     * @param uehara The AI to do the building
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    private void turnBuildAIEasy(Game game, AI uehara) {
        Player ai = game.getPlayer(uehara);
        Inventory inv = game.getInventory(uehara);
        IGameMapManagement map = game.getMap();
        LobbyName lobbyName = game.getLobby().getName();

        List<MapPoint> cities = new ArrayList<>();
        List<MapPoint> settlements = new ArrayList<>();
        Set<IEdge> edges = new HashSet<>();

        MapPoint mp;
        for (int i = 0; i <= 5; i++)
            for (int j = 0; j <= 10; j++) {
                //Why? see GameMapManagement.createIntersectionEdgeNetwork
                if ((i == 0 || i == 5) && j >= 7) break;
                else if ((i == 1 || i == 4) && j >= 9) break;
                mp = MapPoint.IntersectionMapPoint(i, j);
                //Settlement Stuff
                if (map.settlementPlaceable(ai, mp)) settlements.add(mp);
                if (map.settlementUpgradeable(ai, mp)) cities.add(mp);
                //Road Stuff
                for (IEdge e : map.incidentEdges(map.getIntersection(mp)))
                    if (map.roadPlaceable(ai, e)) edges.add(e);
            }

        //Build City for Rock 'n' Roll
        while (inv.get(GRAIN) >= 2 && inv.get(ORE) >= 3 && !cities.isEmpty()) {
            mp = cities.remove((int) (Math.random() * cities.size()));
            map.upgradeSettlement(ai, mp);
            inv.decrease(GRAIN, 2);
            inv.decrease(ORE, 3);
            lobbyService.sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, uehara, mp, CITY));
        }

        //Build Settlement
        while (inv.get(BRICK) >= 1 && inv.get(LUMBER) >= 1 && inv.get(GRAIN) >= 1 && inv.get(WOOL) >= 1 && !settlements
                .isEmpty()) {
            mp = settlements.remove((int) (Math.random() * settlements.size()));
            try {
                map.placeSettlement(ai, mp);
            } catch (GameMapManagement.SettlementMightInterfereWithLongestRoadException e) {
                GameMapManagement.PlayerWithLengthOfLongestRoad a = map.findLongestRoad();
                if (a.getLength() >= 5) {
                    game.setPlayerWithLongestRoad(a.getPlayer());
                    game.setLongestRoadLength(a.getLength());
                } else {
                    game.setPlayerWithLongestRoad(null);
                    game.setLongestRoadLength(0);
                }
                lobbyService.sendToAllInLobby(lobbyName,
                                              new UpdateUniqueCardsListMessage(lobbyName, game.getUniqueCardsList()));
            }
            inv.decrease(BRICK);
            inv.decrease(LUMBER);
            inv.decrease(GRAIN);
            inv.decrease(WOOL);
            lobbyService.sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, uehara, mp, SETTLEMENT));
        }

        List<IEdge> roads = new ArrayList<>(edges);
        //Build Road
        while (inv.get(BRICK) >= 1 && inv.get(LUMBER) >= 1 && !roads.isEmpty()) {
            IEdge edge = roads.remove((int) (Math.random() * roads.size()));
            map.placeRoad(ai, edge);
            inv.decrease(BRICK);
            inv.decrease(LUMBER);
            mp = map.getEdgeMapPoint(edge);
            lobbyService.sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, uehara, mp, ROAD));
        }

        //Buy Dev Card
        while (inv.get(WOOL) >= 1 && inv.get(GRAIN) >= 1 && inv.get(ORE) >= 1 && !game.getBankInventory()
                                                                                      .getDevelopmentCards().isEmpty())
            gameService.onBuyDevelopmentCardRequest(new BuyDevelopmentCardRequest(uehara, lobbyName));
        //Update Victory Points
        gameService.updateVictoryPoints(lobbyName);
    }

    /**
     * Helper method for a hard AI's
     * building phase
     *
     * @param game   The game the AI is in
     * @param uehara The AI to do the building
     *
     * @author Mario Fokken
     * @since 2021-05-15
     */
    private void turnBuildAIHard(Game game, AI uehara) {
        LobbyName lobbyName = game.getLobby().getName();
        Inventory inv = game.getInventory(uehara);
        if (!aiBuildPriority.containsValue(game)) createBuildPriority(game);

        useHarbour(game, uehara);

        //Random chance of buying a card increases steadily
        if (inv.get(GRAIN) > 0 && inv.get(ORE) > 0 && inv.get(WOOL) > 0 && //
            ((int) (Math.random() * 100) < (10 + game.getRound() * 5)))
            gameService.onBuyDevelopmentCardRequest(new BuyDevelopmentCardRequest(uehara, lobbyName));

        //The numbers may not be optimal; not enough data
        if (game.getRound() < 7) {
            //Focus on expanding like crazy
            buildRoad(game, uehara);
            buildSettlement(game, uehara);
        } else if (game.getRound() < 14) {
            //Build a settlement, before making a road
            buildSettlement(game, uehara);
            buildRoad(game, uehara);
            buildCity(game, uehara);
        } else if (game.getRound() < 21) {
            //Twice the building, double the settlements
            while (buildSettlement(game, uehara)) buildSettlement(game, uehara);
            buildCity(game, uehara);
            buildRoad(game, uehara);
        } else {
            //Cities are good
            while (buildCity(game, uehara)) buildCity(game, uehara);
            while (buildSettlement(game, uehara)) buildSettlement(game, uehara);
            buildRoad(game, uehara);
        }

        //Update Victory Points
        gameService.updateVictoryPoints(lobbyName);
    }

    /**
     * Helper method for an easy AI's
     * card playing phase
     *
     * @param game   The game the AI is in
     * @param uehara The AI to do the card playing
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    private void turnPlayCardsAIEasy(Game game, AI uehara) {
        DevelopmentCardList cards = game.getInventory(uehara).getDevelopmentCards();
        LobbyName lobbyName = game.getLobby().getName();
        Inventory inv = game.getInventory(uehara);

        /**
         * Local class
         *
         * @author Mario Fokken
         * @since 2021-05-13
         */
        class randomResource {

            /**
             * Returns a random resource
             *
             * @return random Resource
             */
            private ResourceType getRandomResource() {
                switch ((int) (Math.random() * 4)) {
                    case 0:
                        return BRICK;
                    case 1:
                        return GRAIN;
                    case 2:
                        return LUMBER;
                    case 3:
                        return ORE;
                    default:
                        return WOOL;
                }
            }
        }
        randomResource r = new randomResource();

        if (cards.getAmount(DevelopmentCardType.MONOPOLY_CARD) > 0) {
            playCardAI(game, uehara, DevelopmentCardType.MONOPOLY_CARD, r.getRandomResource(), null);
            return;
        }
        if (cards.getAmount(DevelopmentCardType.ROAD_BUILDING_CARD) > 0) {
            Set<IEdge> edges = new HashSet<>();
            Player ai = game.getPlayer(uehara);
            IGameMapManagement map = game.getMap();
            for (int i = 1; i <= 5; i++) {
                for (int j = 1; j <= 5; j++) {
                    if ((i == 1 || i == 5) && j > 3) break;
                    else if ((i == 2 || i == 4) && j > 4) break;
                    MapPoint mp = MapPoint.HexMapPoint(i, j);
                    for (IEdge edge : map.getEdgesFromHex(mp))
                        if (map.roadPlaceable(ai, edge)) edges.add(edge);
                }
            }
            List<IEdge> roads = new ArrayList<>(edges);
            if (roads.size() > 1) {
                IEdge edge = roads.remove((int) (Math.random() * roads.size()));
                map.placeRoad(ai, edge);
                inv.decrease(DevelopmentCardType.ROAD_BUILDING_CARD);
                lobbyService.sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, uehara,
                                                                                       map.getEdgeMapPoint(edge),
                                                                                       ROAD));
                if (roads.size() > 2) {
                    edge = roads.remove((int) (Math.random() * roads.size()));
                    game.getMap().placeRoad(ai, roads.remove((int) (Math.random() * roads.size())));
                    lobbyService.sendToAllInLobby(lobbyName, new BuildingSuccessfulMessage(lobbyName, uehara,
                                                                                           map.getEdgeMapPoint(edge),
                                                                                           ROAD));
                }
            }
            return;
        }
        if (cards.getAmount(DevelopmentCardType.YEAR_OF_PLENTY_CARD) > 0) {
            playCardAI(game, uehara, DevelopmentCardType.YEAR_OF_PLENTY_CARD, r.getRandomResource(),
                       r.getRandomResource());
            return;
        }
        if (cards.getAmount(DevelopmentCardType.KNIGHT_CARD) > 1)
            playCardAI(game, uehara, DevelopmentCardType.KNIGHT_CARD, null, null);
    }

    /**
     * Helper method for a hard AI's
     * harbour usage
     *
     * @param game   The game the AI is in
     * @param uehara The AI to use the harbour
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    private void useHarbour(Game game, AI uehara) {
        Inventory inv = game.getInventory(uehara);
        if (!harbours.containsKey(uehara)) return;
        ResourceType tradeGive = null;
        ResourceType tradeGet = null;
        //Check if any resource amount is relatively high
        for (ResourceType res : ResourceType.values())
            if (1.0 * inv.get(res) / inv.getResourceAmount() > 0.6) {
                tradeGive = res;
                break;
            }
        if (tradeGive == null) return;
        //Early Game Brick/ Lumber focus, Later Ore/ Grain focus
        if (game.getRound() < 6) {
            if (harbours.get(uehara).contains(IHarbourHex.HarbourResource.LUMBER)) tradeGet = LUMBER;
            if (harbours.get(uehara).contains(IHarbourHex.HarbourResource.BRICK)) tradeGet = BRICK;
        } else {
            if (harbours.get(uehara).contains(IHarbourHex.HarbourResource.GRAIN)) tradeGet = GRAIN;
            if (harbours.get(uehara).contains(IHarbourHex.HarbourResource.ORE)) tradeGet = ORE;
        }
        //Wool
        if (tradeGet == null && harbours.get(uehara).contains(IHarbourHex.HarbourResource.WOOL)) tradeGet = WOOL;
        //Use harbour
        if (tradeGet != null) {
            inv.decrease(tradeGive, 2);
            inv.increase(tradeGet);
            return;
        }
        //Use "any" harbour as last resort
        if (harbours.get(uehara).contains(IHarbourHex.HarbourResource.ANY)) {
            tradeGet = game.getRound() > 6 ? ORE : inv.get(BRICK) >= inv.get(LUMBER) ? LUMBER : BRICK;
            inv.decrease(tradeGive, 3);
            inv.increase(tradeGet);
        }
    }
}