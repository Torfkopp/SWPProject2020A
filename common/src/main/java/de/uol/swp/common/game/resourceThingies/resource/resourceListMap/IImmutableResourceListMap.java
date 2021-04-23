package de.uol.swp.common.game.resourceThingies.resource.resourceListMap;

import de.uol.swp.common.game.resourceThingies.resource.resource.IImmutableResource;

import java.util.Iterator;

/**
 * The interface Immutable resource list map.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public interface IImmutableResourceListMap extends IResourceListMap, Iterable<IImmutableResource> {

    @Override
    Iterator<IImmutableResource> iterator();
}
