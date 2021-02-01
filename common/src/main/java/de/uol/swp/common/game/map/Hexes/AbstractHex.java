package de.uol.swp.common.game.map.Hexes;

/**
 * Abstract class for the interface IGameHex.
 * <p>
 * This abstract class gets extended by all other *Hex classes
 * <p>
 * This abstract class provides one boolean and a corresponding getter and setter
 * to indicate if the robber is on this hex
 *
 * @author Timo Gerken
 * @author Temmo Junkhoff
 */
public abstract class AbstractHex implements IGameHex {

    private boolean robberOnField;

    @Override
    public boolean isRobberOnField() {return robberOnField;}

    @Override
    public void setRobberOnField(boolean robberOnField) {
        this.robberOnField = robberOnField;
    }
}
