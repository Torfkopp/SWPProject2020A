package de.uol.swp.common.game;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ResourceBundle;

public class Resource implements Serializable {

    @Inject
    private static ResourceBundle resourceBundle;

    public Resource create(){
        return new Resource(getType(), getAmount());
    }

    private final ResourceType type;
    private int amount;

    public enum ResourceType {
        LUMBER("game.resources.lumber"),
        BRICK("game.resources.brick"),
        ORE("game.resources.ore"),
        GRAIN("game.resources.grain"),
        WOOL("game.resources.wool");

        private final String attributeName;

        ResourceType(String attributeName) {
            this.attributeName = attributeName;
        }

        @Override
        public String toString() {
            return resourceBundle.getString(getAttributeName());
        }

        public String getAttributeName() {
            return attributeName;
        }
    }

    public Resource(ResourceType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public void decrease(int amount) {
        increase(-amount);
    }

    public void decrease() {
        decrease(-1);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ResourceType getType() {
        return type;
    }

    public void increase(int amount) {
        this.amount += amount;
    }

    public void increase() {
        increase(1);
    }
}
