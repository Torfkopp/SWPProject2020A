package de.uol.swp.common.game.map.Hexes;

/**
 * Abstract class for the interface IGameHex.
 *
 * This abstract class gets extended by all other *Hex classes
 *
 * This abstract class provides one boolean and a corresponding getter and setter
 * to indicate if the robber is on this hex
 *
 * @author Timo Gerken
 * @author Temmo Junkhoff
 */
public abstract class AbstractHex implements IGameHex{
    private boolean robberOnField;

    @Override
    public void setRobberOnField( boolean robberOnField){
        this.robberOnField = robberOnField;
    }

    @Override
    public boolean isRobberOnField() {return robberOnField;}
}
