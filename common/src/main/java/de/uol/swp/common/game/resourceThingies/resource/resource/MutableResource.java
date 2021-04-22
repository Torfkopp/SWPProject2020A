package de.uol.swp.common.game.resourceThingies.resource.resource;

import de.uol.swp.common.game.resourceThingies.resource.ResourceType;

public class MutableResource implements IMutableResource {

    private final ResourceType type;
    private int amount;

    public MutableResource(ResourceType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    @Override
    public MutableResource create() {
        return new MutableResource(getType(), getAmount());
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
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public ResourceType getType() {
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

    @Override
    public ImmutableResource getImmutable(){
        return new ImmutableResource(getType(), getAmount());
    }
}
