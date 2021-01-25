package de.uol.swp.server.game.map.Hexes;

/**
 * Interface for a hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public interface IGameHex {

    type getType();

    enum type {
        Water, Desert, Resource, Harbor
    }

}
