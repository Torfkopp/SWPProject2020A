package de.uol.swp.common.game.resourceThingies.resource.resource;

import de.uol.swp.common.game.resourceThingies.resource.ResourceType;

public class ImmutableResource implements IImmutableResource {

    private final ResourceType type;
    private final int amount;
    public ImmutableResource(ResourceType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public ResourceType getType() {
        return type;
    }

    @Override
    public ImmutableResource getImmutable(){
        return new ImmutableResource(getType(), getAmount());
    }
}
