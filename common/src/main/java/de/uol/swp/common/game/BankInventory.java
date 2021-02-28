package de.uol.swp.common.game;

import java.util.ArrayList;
import java.util.List;

/**
 * The banks inventory
 *
 * @author Alwin Bossert
 * @author Maximilian Lindner
 * @implNote brick, grain, lumber, ore and wool are not used jet
 * @since 2021-02-21
 */
public class BankInventory {

    private int brick = 100;
    private int grain = 100;
    private int lumber = 100;
    private int ore = 100;
    private int wool = 100;

    private int knightCards = 14;
    private int roadBuildingCards = 2;
    private int yearOfPlentyCards = 2;
    private int monopolyCards = 2;
    private int victoryPointCards = 5;
    private List<String> developmentCards;

    /**
     * Constructor
     */
    public BankInventory() {}

    /**
     * Gets the amount of Bricks a bank has in the inventory
     *
     * @return The amount of Bricks
     */
    public int getBrick() {
        return brick;
    }

    /**
     * Sets the amount of Bricks in the bank's inventory
     *
     * @param brick The amount of Bricks to place in the inventory
     */
    public void setBrick(int brick) {
        this.brick = brick;
    }

    /**
     * Sets all cards into a list and returns this list
     *
     * @return List a List having all the cards
     */
    public List<String> getDevelopmentCards() {
        developmentCards = new ArrayList<>();
        for (int i = 0; i < knightCards; i++) {
            developmentCards.add("knightCard");
        }
        for (int i = 0; i < roadBuildingCards; i++) {
            developmentCards.add("roadBuildingCard");
        }
        for (int i = 0; i < yearOfPlentyCards; i++) {
            developmentCards.add("yearOfPlentyCard");
        }
        for (int i = 0; i < monopolyCards; i++) {
            developmentCards.add("monopolyCard");
        }
        for (int i = 0; i < victoryPointCards; i++) {
            developmentCards.add("victoryPointCard");
        }
        return developmentCards;
    }

    /**
     * Gets the amount of Grain a bank has in the inventory
     *
     * @return The amount of Grain
     */
    public int getGrain() {
        return grain;
    }

    /**
     * Sets the amount of Grain in the bank's inventory
     *
     * @param grain The amount of Grain to place in the inventory
     */
    public void setGrain(int grain) {
        this.grain = grain;
    }

    /**
     * Gets the amount of Knight Cards a bank has in the inventory
     *
     * @return The amount of Knight Cards
     */
    public int getKnightCards() {
        return knightCards;
    }

    /**
     * Sets the amount of Knight Cards in the bank's inventory
     *
     * @param knightCards The amount of Knight Cards to place in the inventory
     */
    public void setKnightCards(int knightCards) {
        this.knightCards = knightCards;
    }

    /**
     * Gets the amount of Lumber a bank has in the inventory
     *
     * @return The amount of Lumber
     */
    public int getLumber() {
        return lumber;
    }

    /**
     * Sets the amount of Lumber in the bank's inventory
     *
     * @param lumber The amount of Lumber to place in the inventory
     */
    public void setLumber(int lumber) {
        this.lumber = lumber;
    }

    /**
     * Gets the amount of Monopoly Cards a bank has in the inventory
     *
     * @return The amount of Monopoly Cards
     */
    public int getMonopolyCards() {
        return monopolyCards;
    }

    /**
     * Sets the amount of Monopoly Cards in the bank's inventory
     *
     * @param monopolyCards The amount of Monopoly Cards to place in the inventory
     */
    public void setMonopolyCards(int monopolyCards) {
        this.monopolyCards = monopolyCards;
    }

    /**
     * Gets the amount of Ore a bank has in the inventory
     *
     * @return The amount of Ore
     */
    public int getOre() {
        return ore;
    }

    /**
     * Sets the amount of Ore in the bank's inventory
     *
     * @param ore The amount of Ore to place in the inventory
     */
    public void setOre(int ore) {
        this.ore = ore;
    }

    /**
     * Gets the amount of Road Building Cards a bank has in the inventory
     *
     * @return The amount of Road Building Cards
     */
    public int getRoadBuildingCards() {
        return roadBuildingCards;
    }

    /**
     * Sets the amount of Road Building Cards in the bank's inventory
     *
     * @param roadBuildingCards The amount of Road Building Cards to place in the inventory
     */
    public void setRoadBuildingCards(int roadBuildingCards) {
        this.roadBuildingCards = roadBuildingCards;
    }

    /**
     * Gets the amount of victory point cards the bank has in its inventory
     *
     * @return The amount of victory point cards
     */
    public int getVictoryPointCards() {
        return victoryPointCards;
    }

    /**
     * Gets the amount of Wool a bank has in the inventory
     *
     * @return The amount of Wool
     */
    public int getWool() {
        return wool;
    }

    /**
     * Sets the amount of Wool in the bank's inventory
     *
     * @param wool The amount of Wool to place in the inventory
     */
    public void setWool(int wool) {
        this.wool = wool;
    }

    /**
     * Gets the amount of Year of Plenty Cards a bank has in the inventory
     *
     * @return The amount of Year of Plenty Cards
     */
    public int getYearOfPlentyCards() {
        return yearOfPlentyCards;
    }

    /**
     * Sets the amount of Year of Plenty Cards in the bank's inventory
     *
     * @param yearOfPlentyCards The amount of Year of Plenty Cards to place in the inventory
     */
    public void setYearOfPlentyCards(int yearOfPlentyCards) {
        this.yearOfPlentyCards = yearOfPlentyCards;
    }

    /**
     * Sets the amount of VictoryPointCards of the bankInventory
     *
     * @param victoryPointsCards The amount of Victory Point Cards
     */
    public void setVictoryPointsCards(int victoryPointsCards) {
        this.victoryPointCards = victoryPointsCards;
    }
}
