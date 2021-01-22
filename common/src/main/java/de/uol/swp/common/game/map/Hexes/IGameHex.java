package de.uol.swp.common.game.map.Hexes;

import de.uol.swp.common.game.Renderable;

/**
 * Interface for a hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public interface IGameHex extends Renderable {

    enum type {
        Water, Desert, Resource, Harbor
    }

    type getType();

}
