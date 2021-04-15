package de.uol.swp.common.game;

import de.uol.swp.common.util.Tuple;

import java.util.*;

public abstract class AbstractInventory {

    protected Map<Resource, Integer> resources = new HashMap<>();
    protected Map<DevelopmentCard, Integer> developmentCards = new HashMap<>();

    public void decrease(Resource resource, int i) {
        increase(resource, -i);
    }

    public void decrease(DevelopmentCard developmentCard, int i) {
        increase(developmentCard, -i);
    }

    public void decrease(Resource resource) {
        increase(resource, -1);
    }

    public void decrease(DevelopmentCard developmentCard) {
        increase(developmentCard, -1);
    }

    /**
     * Gets the amount of a resource the bank has in the inventory
     *
     * @return The amount of the resource
     */
    public int get(Resource resource) {
        return resources.get(resource);
    }

    /**
     * Gets the amount of Knight Cards a bank has in the inventory
     *
     * @return The amount of Knight Cards
     */
    public int get(DevelopmentCard developmentCard) {
        return developmentCards.get(developmentCard);
    }

    public List<Tuple<DevelopmentCard, Integer>> getDevelopCardsList() {
        List<Tuple<DevelopmentCard, Integer>> returnList = new LinkedList<>();
        for (DevelopmentCard developmentCard : developmentCards.keySet())
            returnList.add(new Tuple<>(developmentCard, developmentCards.get(developmentCard)));
        return returnList;
    }

    public Map<DevelopmentCard, Integer> getDevelopmentCards() {
        return developmentCards;
    }

    public List<Tuple<Resource, Integer>> getResourceList() {
        List<Tuple<Resource, Integer>> returnList = new LinkedList<>();
        for (Resource resource : resources.keySet())
            returnList.add(new Tuple<>(resource, resources.get(resource)));
        return returnList;
    }

    public Map<Resource, Integer> getResources() {
        return resources;
    }

    public void increase(Resource resource, int i) {
        resources.put(resource, resources.get(resource) + i);
    }

    public void increase(DevelopmentCard developmentCard, int i) {
        developmentCards.put(developmentCard, developmentCards.get(developmentCard) + i);
    }

    public void increase(Resource resource) {
        increase(resource, 1);
    }

    public void increase(DevelopmentCard developmentCard) {
        increase(developmentCard, 1);
    }
}
