package de.uol.swp.common.game.map.Hexes;

/**
 * Interface for a hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public interface IGameHex {

    enum type {
        Water, Desert, Resource, Harbor
    }

    type getType();

}
