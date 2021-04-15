package de.uol.swp.common.game;

import java.util.*;

/**
 * The banks inventory
 *
 * @author Alwin Bossert
 * @author Maximilian Lindner
 * @implNote brick, grain, lumber, ore and wool are not used jet
 * @since 2021-02-21
 */
public class BankInventory extends AbstractInventory {

    public BankInventory() {
        for (Resource resource : Resource.values())
            resources.put(resource, 100);
        developmentCards.put(DevelopmentCard.KNIGHT_CARD, 14);
        developmentCards.put(DevelopmentCard.ROAD_BUILDING_CARD, 2);
        developmentCards.put(DevelopmentCard.YEAR_OF_PLENTY_CARD, 2);
        developmentCards.put(DevelopmentCard.MONOPOLY_CARD, 2);
        developmentCards.put(DevelopmentCard.VICTORY_POINT_CARD, 5);
    }

    public DevelopmentCard getRandomDevelopmentCard(){
        List<DevelopmentCard> temp = new LinkedList<>();
        for (var developmentCard : developmentCards.entrySet())
        temp.addAll(Collections.nCopies(developmentCard.getValue(), developmentCard.getKey()));
        Random random = new Random(); // new Random object, named random
        DevelopmentCard returnCard = temp.get(random.nextInt(temp.size()));
        //decrease(returnCard);
        return returnCard;
    }
}
