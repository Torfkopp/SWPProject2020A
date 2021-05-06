package de.uol.swp.common.game.map.hexes;

import java.io.Serializable;

/**
 * Interface for a hex
 *
 * @author Mario Fokken
 * @author Steven Luong
 * @since 2021-01-16
 */
public interface IGameHex extends Serializable {

    /**
     * Enum for the type of the Hex
     */
    enum HexType {
        WATER,
        DESERT,
        RESOURCE,
        HARBOR,
    }

    /**
     * Gets the type of the field
     *
     * @return The type of the field
     */
    HexType getType();

    /**
     * Checks if the robber is on the field
     *
     * @return True if the robber is on the field, false otherwise
     */
    boolean isRobberOnField();

    /**
     * Sets if the robber is on the field
     *
     * @param robberOnField True if the robber should be on the field, false if not
     */
    void setRobberOnField(boolean robberOnField);
}
