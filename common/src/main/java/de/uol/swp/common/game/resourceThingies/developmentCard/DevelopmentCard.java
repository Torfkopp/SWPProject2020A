package de.uol.swp.common.game.resourceThingies.developmentCard;

import java.util.HashMap;
import java.util.Map;

public class DevelopmentCard implements IDevelopmentCard {



    private final DevelopmentCardType type;
    private int amount;

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    public DevelopmentCard(DevelopmentCardType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    @Override
    public void decrease(int amount) {
        increase(-amount);
    }

    @Override
    public void decrease() {
        decrease(-1);
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public DevelopmentCardType getType() {
        return type;
    }

    @Override
    public void increase(int amount) {
        this.amount += amount;
    }

    @Override
    public void increase() {
        increase(1);
    }
}
