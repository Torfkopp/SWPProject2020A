package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCard;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.*;

import java.util.*;

/**
 * The banks inventory
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public class BankInventory extends AbstractInventory {

    /**
     * Constructor for Bank inventory.
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
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

    /**
     * Gets a random development card.
     *
     * @return The random development card
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
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
