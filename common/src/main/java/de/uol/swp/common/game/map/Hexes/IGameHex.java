package de.uol.swp.common.game.map.Hexes;

/**
 * Interface for a hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public interface IGameHex {

    type getType();

    boolean isRobberOnField();

    void setRobberOnField(boolean robberOnField);

    enum type {
        Water, Desert, Resource, Harbor
    }
}
