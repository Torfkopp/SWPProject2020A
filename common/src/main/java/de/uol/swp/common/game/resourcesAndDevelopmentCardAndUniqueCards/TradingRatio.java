package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards;

import de.uol.swp.common.game.map.hexes.IHarbourHex;

import java.io.Serializable;

/**
 * A class to store a trading ratio.
 *
 * @author Temmo Junkhoff
 * @since 2021-05-04
 */
public class TradingRatio implements Serializable {

    private final IHarbourHex.HarbourResource resource;
    private final int amount;

    /**
     * Constructor.
     *
     * @param resource The resource
     * @param amount   The amount
     *
     * @author Temmo Junkhoff
     * @since 2021-05-04
     */
    public TradingRatio(IHarbourHex.HarbourResource resource, int amount) {
        this.resource = resource;
        this.amount = amount;
    }

    /**
     * Gets the amount.
     *
     * @return The amount
     *
     * @author Temmo Junkhoff
     * @since 2021-05-04
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Gets the resource.
     *
     * @return The resource
     *
     * @author Temmo Junkhoff
     * @since 2021-05-04
     */
    public IHarbourHex.HarbourResource getResource() {
        return resource;
    }
}
