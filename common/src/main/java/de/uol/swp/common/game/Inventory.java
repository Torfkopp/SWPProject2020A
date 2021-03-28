package de.uol.swp.common.game;

/**
 * The player's inventory
 *
 * @author Mario Fokken
 * @author Steven Luong
 * @since 2021-01-16
 */
public class Inventory {

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

    private int knights = 0;
    private boolean longestRoad = false;
    private boolean largestArmy = false;
    private int victoryPoints = 0;

    /**
     * Constructor
     */
    public Inventory() {
    }

    /**
     * Gets the amount of Bricks a player has in their inventory
     *
     * @return The amount of Bricks
     */
    public int getBrick() {
        return brick;
    }

    /**
     * Sets the amount of Bricks in the player's inventory
     *
     * @param brick The amount of Bricks to place in the inventory
     */
    public void setBrick(int brick) {
        this.brick = brick;
    }

    /**
     * Gets the amount of Grain a player has in their inventory
     *
     * @return The amount of Grain
     */
    public int getGrain() {
        return grain;
    }

    /**
     * Sets the amount of Grain in the player's inventory
     *
     * @param grain The amount of Grain to place in the inventory
     */
    public void setGrain(int grain) {
        this.grain = grain;
    }

    /**
     * Gets the amount of Knight Cards a player has in their inventory
     *
     * @return The amount of Knight Cards
     */
    public int getKnightCards() {
        return knightCards;
    }

    /**
     * Sets the amount of Knight Cards in the player's inventory
     *
     * @param knightCards The amount of Knight Cards to place in the inventory
     */
    public void setKnightCards(int knightCards) {
        this.knightCards = knightCards;
    }

    /**
     * Gets the amount of knights
     *
     * @return The amount of knights
     */
    public int getKnights() {
        return knights;
    }

    /**
     * Set the amount of knights
     *
     * @param knights The amount of knights a player has
     */
    public void setKnights(int knights) {
        this.knights = knights;
    }

    /**
     * Gets the amount of Lumber a player has in their inventory
     *
     * @return The amount of Lumber
     */
    public int getLumber() {
        return lumber;
    }

    /**
     * Sets the amount of Lumber in the player's inventory
     *
     * @param lumber The amount of Lumber to place in the inventory
     */
    public void setLumber(int lumber) {
        this.lumber = lumber;
    }

    /**
     * Gets the amount of Monopoly Cards a player has in their inventory
     *
     * @return The amount of Monopoly Cards
     */
    public int getMonopolyCards() {
        return monopolyCards;
    }

    /**
     * Sets the amount of Monopoly Cards in the player's inventory
     *
     * @param monopolyCards The amount of Monopoly Cards to place in the inventory
     */
    public void setMonopolyCards(int monopolyCards) {
        this.monopolyCards = monopolyCards;
    }

    /**
     * Gets the amount of Ore a player has in their inventory
     *
     * @return The amount of Ore
     */
    public int getOre() {
        return ore;
    }

    /**
     * Sets the amount of Ore in the player's inventory
     *
     * @param ore The amount of Ore to place in the inventory
     */
    public void setOre(int ore) {
        this.ore = ore;
    }

    /**
     * Gets the amount of Resource Cards (Bricks, Grain, Lumber, Ore, Wool)
     * the user has in his inventory
     *
     * @return The amount of building resources
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @since 2021-02-24
     */
    public int getResourceAmount() {
        return getBrick() + getWool() + getLumber() + getGrain() + getOre();
    }

    /**
     * Gets the amount of Development Cards the user has in his inventory
     *
     * @return The amount of Development Cards
     *
     * @author Alwin Bossert
     * @author Eric Vuong
     * @since 2021-03-27
     */
    public int getAmountOfDevelopmentCards() {
        return getYearOfPlentyCards() + getMonopolyCards() + getKnightCards() + getVictoryPointCards() + getRoadBuildingCards();
    }

    /**
     * Gets the amount of Road Building Cards a player has in their inventory
     *
     * @return The amount of Road Building Cards
     */
    public int getRoadBuildingCards() {
        return roadBuildingCards;
    }

    /**
     * Sets the amount of Road Building Cards in the player's inventory
     *
     * @param roadBuildingCards The amount of Road Building Cards to place in the inventory
     */
    public void setRoadBuildingCards(int roadBuildingCards) {
        this.roadBuildingCards = roadBuildingCards;
    }

    /**
     * Gets the amount of Victory Point Cards a player has in their inventory
     *
     * @return The amount of Victory Point Cards
     */
    public int getVictoryPointCards() {
        return victoryPointCards;
    }

    /**
     * Sets the amount of Victory Point Cards in the player's inventory
     *
     * @param victoryPointCards The amount of Victory Point Cards to place in the inventory
     */
    public void setVictoryPointCards(int victoryPointCards) {
        this.victoryPointCards = victoryPointCards;
    }

    /**
     * Gets the amount of Victory Points a player currently has
     *
     * @return The amount of Victory Points
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * Sets the amount of Victory Points a player has
     *
     * @param victoryPoints The amount of Victory Points to give to the player
     */
    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    /**
     * Gets the amount of Wool a player has in their inventory
     *
     * @return The amount of Wool
     */
    public int getWool() {
        return wool;
    }

    /**
     * Sets the amount of Wool in the player's inventory
     *
     * @param wool The amount of Wool to place in the inventory
     */
    public void setWool(int wool) {
        this.wool = wool;
    }

    /**
     * Gets the amount of Year of Plenty Cards a player has in their inventory
     *
     * @return The amount of Year of Plenty Cards
     */
    public int getYearOfPlentyCards() {
        return yearOfPlentyCards;
    }

    /**
     * Sets the amount of Year of Plenty Cards in the player's inventory
     *
     * @param yearOfPlentyCards The amount of Year of Plenty Cards to place in the inventory
     */
    public void setYearOfPlentyCards(int yearOfPlentyCards) {
        this.yearOfPlentyCards = yearOfPlentyCards;
    }

    /**
     * Increases the amount of Bricks by the amount
     *
     * @param i The increase
     */
    public void increaseBrick(int i) {
        brick += i;
    }

    /**
     * Increases the amount of Grain by one*
     *
     * @param i The increase
     */
    public void increaseGrain(int i) {
        grain += i;
    }

    /**
     * Increases the amount of KnightCards by one
     *
     * @param i The increase
     */
    public void increaseKnightCards(int i) {
        knightCards += i;
    }

    /**
     * Increases the amount of Knights by one
     */
    public void increaseKnights(int i) {
        knights += i;
    }

    /**
     * Increases the amount of Lumber by one
     *
     * @param i The increase
     */
    public void increaseLumber(int i) {
        lumber += i;
    }

    /**
     * Increases the amount of MonopolyCards by one
     *
     * @param i The increase
     */
    public void increaseMonopolyCards(int i) {
        monopolyCards += i;
    }

    /**
     * Increases the amount of Ore by one
     *
     * @param i The increase
     */
    public void increaseOre(int i) {
        ore += i;
    }

    /**
     * Increases the amount of RoadBuildingCards by one
     *
     * @param i The increase
     */
    public void increaseRoadBuildingCards(int i) {
        roadBuildingCards += i;
    }

    /**
     * Increases the amount of VictoryPointCards by one
     *
     * @param i The increase
     */
    public void increaseVictoryPointCards(int i) {
        victoryPointCards += i;
    }

    /**
     * Increases the amount of Wool by one
     *
     * @param i The increase
     */
    public void increaseWool(int i) {
        wool += i;
    }

    /**
     * Increases the amount of YearOfPlentyCards by one
     *
     * @param i The increase
     */
    public void increaseYearOfPlentyCards(int i) {
        yearOfPlentyCards += i;
    }

    /**
     * Gets whether the player holds the unique "Largest Army" card
     *
     * @return true if the player has the "Largest Army" card, false if not
     */
    public boolean isLargestArmy() {
        return largestArmy;
    }

    /**
     * Sets whether the player holds the unique "Largest Army" card
     *
     * @param largestArmy true if the player has the Largest Army, false if not
     */
    public void setLargestArmy(boolean largestArmy) {
        this.largestArmy = largestArmy;
    }

    /**
     * Gets whether the player holds the unique "Longest Road" card
     *
     * @return true if the player has the "Longest Road" card, false if not
     */
    public boolean isLongestRoad() {
        return longestRoad;
    }

    /**
     * Sets whether the player holds the unique "Longest Road" card
     *
     * @param longestRoad true if the player has the Longest Road, false if not
     */
    public void setLongestRoad(boolean longestRoad) {
        this.longestRoad = longestRoad;
    }
}
