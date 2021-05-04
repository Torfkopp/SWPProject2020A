package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards;

import de.uol.swp.common.game.map.hexes.IHarborHex;

import java.io.Serializable;

/**
 * The type Trading ratio.
 *
 * @author Temmo Junkhoff
 * @since 2021-05-04
 */
public class TradingRatio implements Serializable {

    private final IHarborHex.HarborResource resource;
    private final int amount;

    /**
     * Constructor for Trading ratio.
     *
     * @param resource The resource
     * @param amount   The amount
     *
     * @author Temmo Junkhoff
     * @since 2021-05-04
     */
    public TradingRatio(IHarborHex.HarborResource resource, int amount) {
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
    public IHarborHex.HarborResource getResource() {
        return resource;
    }
}
