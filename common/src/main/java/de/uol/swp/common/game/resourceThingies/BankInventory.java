package de.uol.swp.common.game.resourceThingies;

import de.uol.swp.common.game.resourceThingies.developmentCard.DevelopmentCard;
import de.uol.swp.common.game.resourceThingies.developmentCard.DevelopmentCardList;
import de.uol.swp.common.game.resourceThingies.developmentCard.DevelopmentCardType;
import de.uol.swp.common.game.resourceThingies.resource.*;

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
        List<IResource> tempResourceList = new LinkedList<>();
        List<DevelopmentCard> tempDevelopmentCardList = new LinkedList<>();
        for (ResourceType resource : ResourceType.values())
            tempResourceList.add(new Resource(resource, 100));
        tempDevelopmentCardList.add(new DevelopmentCard(DevelopmentCardType.KNIGHT_CARD, 14));
        tempDevelopmentCardList.add(new DevelopmentCard(DevelopmentCardType.ROAD_BUILDING_CARD, 2));
        tempDevelopmentCardList.add(new DevelopmentCard(DevelopmentCardType.YEAR_OF_PLENTY_CARD, 2));
        tempDevelopmentCardList.add(new DevelopmentCard(DevelopmentCardType.MONOPOLY_CARD, 2));
        tempDevelopmentCardList.add(new DevelopmentCard(DevelopmentCardType.VICTORY_POINT_CARD, 5));
        resources = ResourceList.createResourceListMapFromList(tempResourceList);
        developmentCards = DevelopmentCardList.createDevelopmentCardListFromList(tempDevelopmentCardList);
    }

    public DevelopmentCardType getRandomDevelopmentCard() {
        List<DevelopmentCardType> temp = new LinkedList<>();
        for (DevelopmentCard developmentCard : developmentCards)
            temp.addAll(Collections.nCopies(developmentCard.getAmount(), developmentCard.getType()));
        Random random = new Random(); // new Random object, named random
        DevelopmentCardType returnCard = temp.get(random.nextInt(temp.size()));
        //decrease(returnCard);
        return returnCard;
    }
}
