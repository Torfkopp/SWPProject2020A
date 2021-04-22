package de.uol.swp.common.game.resourceThingies.resource.resourceListMap;

import de.uol.swp.common.game.resourceThingies.resource.resource.IImmutableResource;

import java.util.Iterator;

public interface IImmutableResourceListMap extends IResourceListMap, Iterable<IImmutableResource> {

    @Override
    Iterator<IImmutableResource> iterator();
}
