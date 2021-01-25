package de.uol.swp.server.game;

import de.uol.swp.common.user.User;

/**
 * The player's inventory
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public class Inventory {


    private final User player;

    private int totalResources = 0;

    private int brick = 0;
    private int grain = 0;
    private int lumber = 0;
    private int ore = 0;
    private int wool = 0;

    private int victoryPointCards = 0;
    private int knightCards = 0;
    private int roadBuildingCards = 0;
    private int yearOfPlentyCards = 0;
    private int monopolyCards = 0;

    private boolean longestRoad = false;
    private boolean largestArmy = false;

    public Inventory(User player) {
        this.player = player;
    }

    public User getPlayer() { return player;  }

    public int getTotalResources() {
        return totalResources;
    }

    public void setTotalResources(int totalResources) {
        this.totalResources = totalResources;
    }

    public int getBrick() {
        return brick;
    }

    public void setBrick(int brick) {
        this.brick = brick;
    }

    public int getGrain() {
        return grain;
    }

    public void setGrain(int grain) {
        this.grain = grain;
    }

    public int getLumber() {
        return lumber;
    }

    public void setLumber(int lumber) {
        this.lumber = lumber;
    }

    public int getOre() {
        return ore;
    }

    public void setOre(int ore) {
        this.ore = ore;
    }

    public int getWool() {
        return wool;
    }

    public void setWool(int wool) {
        this.wool = wool;
    }

    public int getVictoryPointCards() {
        return victoryPointCards;
    }

    public void setVictoryPointCards(int victoryPointCards) {
        this.victoryPointCards = victoryPointCards;
    }

    public int getKnightCards() {
        return knightCards;
    }

    public void setKnightCards(int knightCards) {
        this.knightCards = knightCards;
    }

    public int getRoadBuildingCards() {
        return roadBuildingCards;
    }

    public void setRoadBuildingCards(int roadBuildingCards) {
        this.roadBuildingCards = roadBuildingCards;
    }

    public int getYearOfPlentyCards() {
        return yearOfPlentyCards;
    }

    public void setYearOfPlentyCards(int yearOfPlentyCards) {
        this.yearOfPlentyCards = yearOfPlentyCards;
    }

    public int getMonopolyCards() {
        return monopolyCards;
    }

    public void setMonopolyCards(int monopolyCards) {
        this.monopolyCards = monopolyCards;
    }

    public boolean isLongestRoad() {
        return longestRoad;
    }

    public void setLongestRoad(boolean longestRoad) {
        this.longestRoad = longestRoad;
    }

    public boolean isLargestArmy() {
        return largestArmy;
    }

    public void setLargestArmy(boolean largestArmy) {
        this.largestArmy = largestArmy;
    }
}
