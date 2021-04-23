package de.uol.swp.common.game.resourceThingies;

import de.uol.swp.common.game.resourceThingies.developmentCard.DevelopmentCard;
import de.uol.swp.common.game.resourceThingies.developmentCard.DevelopmentCardListMap;
import de.uol.swp.common.game.resourceThingies.resource.ResourceType;
import de.uol.swp.common.game.resourceThingies.resource.resource.IImmutableResource;
import de.uol.swp.common.game.resourceThingies.resource.resource.MutableResource;
import de.uol.swp.common.game.resourceThingies.resource.resourceListMap.MutableResourceListMap;

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
        List<IImmutableResource> tempResourceList = new LinkedList<>();
        List<DevelopmentCard> tempDevelopmentCardList = new LinkedList<>();
        for (ResourceType resource : ResourceType.values())
            tempResourceList.add(new MutableResource(resource, 100));
        tempDevelopmentCardList.add(new DevelopmentCard(DevelopmentCard.DevelopmentCardType.KNIGHT_CARD, 14));
        tempDevelopmentCardList.add(new DevelopmentCard(DevelopmentCard.DevelopmentCardType.ROAD_BUILDING_CARD, 2));
        tempDevelopmentCardList.add(new DevelopmentCard(DevelopmentCard.DevelopmentCardType.YEAR_OF_PLENTY_CARD, 2));
        tempDevelopmentCardList.add(new DevelopmentCard(DevelopmentCard.DevelopmentCardType.MONOPOLY_CARD, 2));
        tempDevelopmentCardList.add(new DevelopmentCard(DevelopmentCard.DevelopmentCardType.VICTORY_POINT_CARD, 5));
        resources = MutableResourceListMap.createResourceListMapFromList(tempResourceList);
        developmentCards = DevelopmentCardListMap.createDevelopmentCardListMapFromList(tempDevelopmentCardList);
    }

    public DevelopmentCard.DevelopmentCardType getRandomDevelopmentCard(){
        List<DevelopmentCard.DevelopmentCardType> temp = new LinkedList<>();
        for (DevelopmentCard developmentCard : developmentCards)
        temp.addAll(Collections.nCopies(developmentCard.getAmount(), developmentCard.getType()));
        Random random = new Random(); // new Random object, named random
        DevelopmentCard.DevelopmentCardType returnCard = temp.get(random.nextInt(temp.size()));
        //decrease(returnCard);
        return returnCard;
    }
}
