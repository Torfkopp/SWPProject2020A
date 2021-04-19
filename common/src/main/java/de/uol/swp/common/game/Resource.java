package de.uol.swp.common.game;

public class Resource {

    private final ResourceType type;
    private int amount;
    public enum ResourceType {
        LUMBER("game.resource.lumber"),
        BRICK("game.resource.brick"),
        ORE("game.resource.ore"),
        GRAIN("game.resource.grain"),
        WOOL("game.resource.wool");

        private final String attributeName;

        ResourceType(String attributeName) {
            this.attributeName = attributeName;
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
