package de.uol.swp.common.game.resourceThingies.resource.resource;

import java.io.Serializable;

public interface IMutableResource extends Serializable, IImmutableResource {

    MutableResource create();

    void decrease(int amount);

    void decrease();

    void setAmount(int amount);

    void increase(int amount);

    void increase();

}
