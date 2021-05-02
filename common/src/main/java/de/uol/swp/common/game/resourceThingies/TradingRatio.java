package de.uol.swp.common.game.resourceThingies;

import de.uol.swp.common.game.map.hexes.IHarborHex;

import java.io.Serializable;

public class TradingRatio implements Serializable {

    private final IHarborHex.HarborResource resource;
    private final int amount;

    public TradingRatio(IHarborHex.HarborResource resource, int amount) {
        this.resource = resource;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public IHarborHex.HarborResource getResource() {
        return resource;
    }
}
