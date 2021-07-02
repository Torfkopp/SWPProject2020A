package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCard;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.*;
import de.uol.swp.common.util.Util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The Bank's inventory
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
            tempResourceList.add(new Resource(resource, -1));
        tempDevelopmentCardList.add(new DevelopmentCard(DevelopmentCardType.KNIGHT_CARD, 14));
        tempDevelopmentCardList.add(new DevelopmentCard(DevelopmentCardType.ROAD_BUILDING_CARD, 2));
        tempDevelopmentCardList.add(new DevelopmentCard(DevelopmentCardType.YEAR_OF_PLENTY_CARD, 2));
        tempDevelopmentCardList.add(new DevelopmentCard(DevelopmentCardType.MONOPOLY_CARD, 2));
        tempDevelopmentCardList.add(new DevelopmentCard(DevelopmentCardType.VICTORY_POINT_CARD, 5));
        resources = ResourceList.createResourceListFromList(tempResourceList);
        developmentCards = DevelopmentCardList.createDevelopmentCardListFromList(tempDevelopmentCardList);
    }

    @Override
    public void decrease(ResourceType resource, int i) {}

    @Override
    public void decrease(DevelopmentCardType developmentCard, int i) {}

    @Override
    public void decrease(ResourceType resource) {}

    @Override
    public void decrease(DevelopmentCardType developmentCard) {}

    @Override
    public void increase(ResourceType resource, int i) {}

    @Override
    public void increase(DevelopmentCardType developmentCard, int i) {}

    @Override
    public void increase(ResourceType resource) {}

    @Override
    public void increase(DevelopmentCardType developmentCard) {}

    @Override
    public void increaseAll(int i) {}

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
        try {
            return temp.get(Util.randomInt(temp.size()));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
