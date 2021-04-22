package de.uol.swp.common.game.resourceThingies.resource.resourceListMap;

import de.uol.swp.common.game.resourceThingies.resource.ResourceType;
import de.uol.swp.common.game.resourceThingies.resource.resource.IMutableResource;

import java.util.Iterator;

public interface IMutableResourceListMap extends IResourceListMap, Iterable<IMutableResource> {

    @Override
    Iterator<IMutableResource> iterator();

    IMutableResourceListMap create();

    void decrease(ResourceType resource, int amount);

    void decrease(ResourceType resource);

    void increase(ResourceType resource, int amount);

    void increase(ResourceType resource);

    void set(ResourceType resource, int amount);
}
