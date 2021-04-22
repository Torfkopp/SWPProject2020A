package de.uol.swp.common.game.resourceThingies.resource.resourceListMap;

import de.uol.swp.common.game.resourceThingies.resource.ResourceType;
import de.uol.swp.common.game.resourceThingies.resource.resource.IImmutableResource;

public interface IResourceListMap{

    IImmutableResource get(ResourceType resource);

    int getAmount(ResourceType resource);
}
