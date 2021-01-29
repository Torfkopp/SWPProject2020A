package de.uol.swp.common.game.map.Hexes;

/**
 * Interface for a land hex
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public interface ILandHex extends IGameHex {

    boolean isRobberOnField();
    void setRobberOnField(boolean robberOnField);

}
