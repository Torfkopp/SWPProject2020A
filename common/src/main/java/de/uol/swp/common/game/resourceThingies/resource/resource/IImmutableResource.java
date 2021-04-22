package de.uol.swp.common.game.resourceThingies.resource.resource;

import de.uol.swp.common.game.resourceThingies.resource.ResourceType;

import java.io.Serializable;

public interface IImmutableResource extends Serializable {

    int getAmount();

    ImmutableResource getImmutable();

    ResourceType getType();
}
