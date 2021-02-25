package de.uol.swp.common.game;

import de.uol.swp.common.user.User;

/**
 * The player's inventory
 *
 * @author Mario Fokken
 * @author Steven Luong
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

    private int victoryPoints = 0;

    /**
     * Constructor
     *
     * @param player The player who this inventory belongs to
     */
    public Inventory(User player) {
        this.player = player;
    }

    /**
     * Gets the player who this inventory belongs to
     *
     * @return The player who this inventory belongs to
     */
    public User getPlayer() {
        return player;
    }

    /**
     * Gets the total amount of resources a player has in their inventory
     *
     * @return The total amount of resources
     */
    public int getTotalResources() {
        return totalResources;
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
     * Increases the amount of Bricks by the amount
     *
     * @param i The increase
     */
    public void increaseBrick(int i) {
        brick += i;
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
     * Sets the amount of Grain in the player's inventory
     *
     * @param grain The amount of Grain to place in the inventory
     */
    public void setGrain(int grain) {
        this.grain = grain;
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
     * Gets the amount of Grain a player has in their inventory
     *
     * @return The amount of Grain
     */
    public int getGrain() {
        return grain;
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
     * Increases the amount of Lumber by one
     *
     * @param i The increase
     */
    public void increaseLumber(int i) {
        lumber += i;
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
     * Increases the amount of Ore by one
     *
     * @param i The increase
     */
    public void increaseOre(int i) {
        ore += i;
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
     * Sets the amount of Monopoly Cards in the player's inventory
     *
     * @param monopolyCards The amount of Monopoly Cards to place in the inventory
     */
    public void setMonopolyCards(int monopolyCards) {
        this.monopolyCards = monopolyCards;
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
     * Gets the amount of Wool a player has in their inventory
     *
     * @return The amount of Wool
     */
    public int getWool() {
        return wool;
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
     * Gets the player who this inventory belongs to
     *
     * @return The player who this inventory belongs to
     */
    public User getPlayer() {
        return player;
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
     * Sets the amount of Road Building Cards in the player's inventory
     *
     * @param roadBuildingCards The amount of Road Building Cards to place in the inventory
     */
    public void setRoadBuildingCards(int roadBuildingCards) {
        this.roadBuildingCards = roadBuildingCards;
    }

    /**
     * Gets the total amount of resources a player has in their inventory
     *
     * @return The total amount of resources
     */
    public int getTotalResources() {
        return totalResources;
    }

    /**
     * Sets the total amount of resources in the player's inventory
     *
     * @param totalResources The total amount of resources to place in the inventory
     */
    public void setTotalResources(int totalResources) {
        this.totalResources = totalResources;
    }

    /**
     * Increases the amount of RoadBuildingCards by one
     */
    public void increaseRoadBuildingCards() {
        roadBuildingCards++;
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
     * Increases the amount of YearOfPlentyCards by one
     */
    public void increaseYearOfPlentyCards() {
        yearOfPlentyCards++;
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
     * Sets the amount of Wool in the player's inventory
     *
     * @param wool The amount of Wool to place in the inventory
     */
    public void setWool(int wool) {
        this.wool = wool;
    }

    /**
     * Increases the amount of MonopolyCards by one
     */
    public void increaseMonopolyCards() {
        monopolyCards++;
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
     * Sets the amount of Year of Plenty Cards in the player's inventory
     *
     * @param yearOfPlentyCards The amount of Year of Plenty Cards to place in the inventory
     */
    public void setYearOfPlentyCards(int yearOfPlentyCards) {
        this.yearOfPlentyCards = yearOfPlentyCards;
    }

    //
    // OTHERS
    //

    /**
     * Increases the amount of Knights by one
     */
    public void increaseKnights() {
        knights++;
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
