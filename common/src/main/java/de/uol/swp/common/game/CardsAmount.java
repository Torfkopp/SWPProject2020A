package de.uol.swp.common.game;

import de.uol.swp.common.user.Actor;

import java.io.Serializable;

/**
 * A class to store the amount a resource cards and development cards of a player.
 *
 * @author Temmo Junkhoff
 * @since 2021-05-04
 */
public class CardsAmount implements Serializable {

    private final Actor user;
    private final int resourceCardsAmount;
    private final int developmentCardsAmount;

    /**
     * Constructor.
     *
     * @param user                   The user to whom the amount of cards belong
     * @param resourceCardsAmount    The amount of resource cards of the user
     * @param developmentCardsAmount The amount of development cards of the user
     *
     * @author Temmo Junkhoff
     * @since 2021-05-04
     */
    public CardsAmount(Actor user, int resourceCardsAmount, int developmentCardsAmount) {
        this.user = user;
        this.resourceCardsAmount = resourceCardsAmount;
        this.developmentCardsAmount = developmentCardsAmount;
    }

    /**
     * Gets the development cards amount.
     *
     * @return The development cards amount
     *
     * @author Temmo Junkhoff
     * @since 2021-05-04
     */
    public int getDevelopmentCardsAmount() {
        return developmentCardsAmount;
    }

    /**
     * Gets the resource cards amount.
     *
     * @return The resource cards amount
     *
     * @author Temmo Junkhoff
     * @since 2021-05-04
     */
    public int getResourceCardsAmount() {
        return resourceCardsAmount;
    }

    /**
     * Gets the user.
     *
     * @return The user
     *
     * @author Temmo Junkhoff
     * @since 2021-05-04
     */
    public Actor getUser() {
        return user;
    }
}
