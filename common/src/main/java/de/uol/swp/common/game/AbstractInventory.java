package de.uol.swp.common.game;

import de.uol.swp.common.util.Tuple;

import java.util.*;

public abstract class AbstractInventory {

    protected Map<Resource.ResourceType, Integer> resources = new HashMap<>();
    protected Map<DevelopmentCard.DevelopmentCardType, Integer> developmentCards = new HashMap<>();

    public void decrease(Resource.ResourceType resource, int i) {
        increase(resource, -i);
    }

    public void decrease(DevelopmentCard.DevelopmentCardType developmentCard, int i) {
        increase(developmentCard, -i);
    }

    public void decrease(Resource.ResourceType resource) {
        increase(resource, -1);
    }

    public void decrease(DevelopmentCard.DevelopmentCardType developmentCard) {
        increase(developmentCard, -1);
    }

    /**
     * Gets the amount of a resource the bank has in the inventory
     *
     * @return The amount of the resource
     */
    public int get(Resource.ResourceType resource) {
        return resources.get(resource);
    }

    /**
     * Gets the amount of Knight Cards a bank has in the inventory
     *
     * @return The amount of Knight Cards
     */
    public int get(DevelopmentCard.DevelopmentCardType developmentCard) {
        return developmentCards.get(developmentCard);
    }

    public List<Tuple<DevelopmentCard.DevelopmentCardType, Integer>> getDevelopCardsList() {
        List<Tuple<DevelopmentCard.DevelopmentCardType, Integer>> returnList = new LinkedList<>();
        for (DevelopmentCard.DevelopmentCardType developmentCard : developmentCards.keySet())
            returnList.add(new Tuple<>(developmentCard, developmentCards.get(developmentCard)));
        return returnList;
    }

    public Map<DevelopmentCard.DevelopmentCardType, Integer> getDevelopmentCards() {
        return developmentCards;
    }

    public List<Tuple<Resource.ResourceType, Integer>> getResourceList() {
        List<Tuple<Resource.ResourceType, Integer>> returnList = new LinkedList<>();
        for (Resource.ResourceType resource : resources.keySet())
            returnList.add(new Tuple<>(resource, resources.get(resource)));
        return returnList;
    }

    public Map<Resource.ResourceType, Integer> getResources() {
        return resources;
    }

    public void increase(Resource.ResourceType resource, int i) {
        resources.put(resource, resources.get(resource) + i);
    }

    public void increase(DevelopmentCard.DevelopmentCardType developmentCard, int i) {
        developmentCards.put(developmentCard, developmentCards.get(developmentCard) + i);
    }

    public void increase(Resource.ResourceType resource) {
        increase(resource, 1);
    }

    public void increase(DevelopmentCard.DevelopmentCardType developmentCard) {
        increase(developmentCard, 1);
    }
}
